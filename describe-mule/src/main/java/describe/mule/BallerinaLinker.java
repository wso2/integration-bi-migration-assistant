/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package describe.mule;

import common.BallerinaModel.ClassDef;
import common.BallerinaModel.Function;
import common.BallerinaModel.Remote;
import common.BallerinaModel.Resource;
import common.BallerinaModel.Service;
import common.BallerinaModel.TextDocument;
import describe.model.DescriptionModel.BallerinaLink;
import describe.model.DescriptionModel.Construct;
import mule.common.apispec.ApiKitFlowName;
import mule.v4.Context;
import mule.v4.model.MuleModel;
import mule.v4.model.MuleModel.AnypointMqSubscriber;
import mule.v4.model.MuleModel.ApiKitConfig;
import mule.v4.model.MuleModel.FileListener;
import mule.v4.model.MuleModel.HttpListener;
import mule.v4.model.MuleModel.MuleRecord;
import mule.v4.model.MuleModel.PubSubMessageListener;
import mule.v4.model.MuleModel.Scheduler;
import mule.v4.model.MuleModel.VMListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

import static common.ConversionUtils.escapeIdentifier;
import static mule.v4.ConversionUtils.convertToBalIdentifier;

/**
 * Finds the Ballerina construct each flow became. It applies the converter's own naming rules
 * ({@code MuleToBalConverter}) and then checks that the construct exists in the generated code; if not, the link is
 * unknown. Resource paths are compared by shape, because the converter may rename path parameters.
 */
final class BallerinaLinker {

    private static final String JOB_CLASS_NAME = "Job";

    private final Context ctx;
    private final Set<String> functionNames = new HashSet<>();
    private final Set<String> classNames = new HashSet<>();
    private final List<Service> services = new ArrayList<>();
    private final Map<String, MuleModel.Flow> routerFlows;

    BallerinaLinker(Context ctx, List<TextDocument> documents) {
        this.ctx = ctx;
        for (TextDocument document : documents) {
            for (Function function : document.functions()) {
                functionNames.add(function.functionName());
            }
            for (ClassDef classDef : document.classDefs()) {
                classNames.add(classDef.className());
            }
            services.addAll(document.services());
        }
        routerFlows = ApiDescriber.routerFlows(ctx);
    }

    Optional<BallerinaLink> link(String flowName, Optional<MuleRecord> source) {
        if (source.isEmpty() || source.get() instanceof FileListener) {
            return Optional.of(function(convertToBalIdentifier(flowName)));
        }
        MuleRecord record = source.get();
        if (record instanceof VMListener listener) {
            String name = ctx.projectCtx.vmQueueNameToBalFuncMap.getOrDefault(listener.queueName(),
                    convertToBalIdentifier(flowName));
            return Optional.of(function(name));
        }
        if (record instanceof AnypointMqSubscriber subscriber) {
            return Optional.of(anypointMqService(subscriber));
        }
        if (record instanceof PubSubMessageListener listener) {
            return Optional.of(pubSubService(listener));
        }
        if (record instanceof HttpListener listener) {
            return Optional.of(httpResource(listener, isRouterFlow(flowName)));
        }
        if (record instanceof ApiKitConfig apiKit) {
            return apiKitResource(flowName, apiKit);
        }
        if (record instanceof Scheduler) {
            // genSchedulerSource always names the class Job, so two scheduler flows cannot be told apart
            return Optional.of(new BallerinaLink(Construct.JOB_CLASS, Optional.of(JOB_CLASS_NAME), Optional.empty(),
                    Optional.empty(), Optional.empty(), classNames.contains(JOB_CLASS_NAME)));
        }
        return Optional.empty();
    }

    private BallerinaLink function(String name) {
        return new BallerinaLink(Construct.FUNCTION, Optional.of(name), Optional.empty(), Optional.empty(),
                Optional.empty(), functionNames.contains(name));
    }

    // genAnypointMqSource: a service attached as "<config-ref>" to the listener named after the config-ref, with a
    // remote onMessage. Two MQ flows with the same config-ref cannot be told apart by these names.
    private BallerinaLink anypointMqService(AnypointMqSubscriber subscriber) {
        String serviceName = "\"" + escapeIdentifier(subscriber.configRef()) + "\"";
        String listenerName = convertToBalIdentifier(subscriber.configRef());
        boolean found = false;
        for (Service service : services) {
            if (service.basePath().equals(serviceName) && service.listenerRefs().contains(listenerName)
                    && hasOnMessage(service)) {
                found = true;
            }
        }
        return new BallerinaLink(Construct.REMOTE_METHOD, Optional.of("onMessage"), Optional.of(serviceName),
                Optional.empty(), Optional.empty(), found);
    }

    // genPubSubSource: a service with no base path on the listener escapeIdentifier(config-ref), with a remote
    // onMessage. Two Pub/Sub flows with the same config-ref cannot be told apart by these names.
    private BallerinaLink pubSubService(PubSubMessageListener listener) {
        String listenerName = escapeIdentifier(listener.configRef());
        boolean found = false;
        for (Service service : services) {
            if (service.listenerRefs().contains(listenerName) && hasOnMessage(service)) {
                found = true;
            }
        }
        return new BallerinaLink(Construct.REMOTE_METHOD, Optional.of("onMessage"), Optional.empty(),
                Optional.empty(), Optional.empty(), found);
    }

    private static boolean hasOnMessage(Service service) {
        for (Remote remote : service.remoteFunctions()) {
            if (remote.function().functionName().equals("onMessage")) {
                return true;
            }
        }
        return false;
    }

    // genBalService: one resource per allowed method at the listener path, on the service of the listener
    // convertToBalIdentifier(config-ref). A router flow's resource is the fallback for paths no operation matches.
    // The method is named only when there is one.
    private BallerinaLink httpResource(HttpListener listener, boolean routerFlow) {
        String listenerName = convertToBalIdentifier(listener.configRef());
        String wantedShape = shape(converterListenerPath(listener, routerFlow));
        Optional<Service> foundService = Optional.empty();
        Optional<Resource> foundResource = Optional.empty();
        boolean found = true;
        for (String method : listener.allowedMethods()) {
            Optional<Resource> resource = Optional.empty();
            for (Service service : services) {
                if (service.listenerRefs().contains(listenerName) && resource.isEmpty()) {
                    resource = resourceWithShape(service, method, wantedShape);
                    if (resource.isPresent() && foundService.isEmpty()) {
                        foundService = Optional.of(service);
                        foundResource = resource;
                    }
                }
            }
            found = found && resource.isPresent();
        }
        Optional<String> method = listener.allowedMethods().length == 1 ? Optional.of(listener.allowedMethods()[0])
                : Optional.empty();
        return new BallerinaLink(Construct.RESOURCE, Optional.empty(), foundService.map(Service::basePath), method,
                writablePath(listener.resourcePath(), foundResource), found);
    }

    // genApiKitSource: a resource with the flow's method, at the spec path (or the flow-name path) behind the
    // router's path prefix, added to the router's service
    private Optional<BallerinaLink> apiKitResource(String flowName, ApiKitConfig apiKit) {
        Optional<ApiKitFlowName> route = ApiKitFlowName.parse(flowName);
        if (route.isEmpty()) {
            return Optional.empty();
        }
        String prefix = ctx.projectCtx.apiKitResourcePrefixes.getOrDefault(apiKit.name(), "");
        String wantedShape = shape(prefix + "/" + route.get().path());
        String routerListenerPath = "";
        MuleModel.Flow routerFlow = routerFlows.get(apiKit.name());
        if (routerFlow != null && routerFlow.source().isPresent()
                && routerFlow.source().get() instanceof HttpListener listener) {
            routerListenerPath = listener.resourcePath();
        }
        for (Service service : services) {
            Optional<Resource> resource = resourceWithShape(service, route.get().method(), wantedShape);
            if (resource.isPresent()) {
                return Optional.of(new BallerinaLink(Construct.RESOURCE, Optional.empty(),
                        Optional.of(service.basePath()), Optional.of(route.get().method()),
                        writablePath(routerListenerPath, resource), true));
            }
        }
        return Optional.of(new BallerinaLink(Construct.RESOURCE, Optional.empty(), Optional.empty(),
                Optional.of(route.get().method()), Optional.empty(), false));
    }

    private boolean isRouterFlow(String flowName) {
        for (MuleModel.Flow routerFlow : routerFlows.values()) {
            if (routerFlow.name().equals(flowName)) {
                return true;
            }
        }
        return false;
    }

    // The converter resolves a router's ${...} listener path to its value; other listener paths are used as written.
    // The value is only compared here, never written.
    private String converterListenerPath(HttpListener listener, boolean routerFlow) {
        return routerFlow ? ctx.resolveProjectProperty(listener.resourcePath()).orElse(listener.resourcePath())
                : listener.resourcePath();
    }

    // A path that comes from a ${...} property holds its value, so it is left out of the JSON (rule 5)
    private static Optional<String> writablePath(String listenerPath, Optional<Resource> resource) {
        if (listenerPath.contains("${") || resource.isEmpty()) {
            return Optional.empty();
        }
        return Optional.of(resource.get().path());
    }

    private static Optional<Resource> resourceWithShape(Service service, String method, String wantedShape) {
        for (Resource resource : service.resources()) {
            if (resource.resourceMethodName().equals(method) && shape(resource.path()).equals(wantedShape)) {
                return Optional.of(resource);
            }
        }
        return Optional.empty();
    }

    // A path with its parameters blanked, so that /rates/{code} and rates/[string codeParam] compare equal. Ballerina
    // escapes (fraud\-risk, 'returns) are removed first. A * or a rest parameter becomes **.
    static String shape(String path) {
        List<String> segments = new ArrayList<>();
        for (String segment : path.split("/")) {
            String plain = segment.replace("\\", "");
            if (plain.startsWith("'")) {
                plain = plain.substring(1);
            }
            if (plain.isEmpty() || plain.equals(".")) {
                continue;
            }
            if (plain.equals("*") || (plain.startsWith("[") && plain.contains("..."))) {
                segments.add("**");
            } else if (plain.startsWith("[") || plain.startsWith("{") || plain.startsWith("#[")) {
                segments.add("{}");
            } else {
                segments.add(plain);
            }
        }
        return String.join("/", segments);
    }
}
