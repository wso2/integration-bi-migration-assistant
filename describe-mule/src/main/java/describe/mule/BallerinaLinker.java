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

import common.BallerinaModel.Function;
import common.BallerinaModel.Remote;
import common.BallerinaModel.Service;
import common.BallerinaModel.TextDocument;
import describe.model.DescriptionModel.BallerinaLink;
import describe.model.DescriptionModel.Construct;
import mule.v4.Context;
import mule.v4.model.MuleModel.AnypointMqSubscriber;
import mule.v4.model.MuleModel.FileListener;
import mule.v4.model.MuleModel.MuleRecord;
import mule.v4.model.MuleModel.VMListener;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static common.ConversionUtils.escapeIdentifier;
import static mule.v4.ConversionUtils.convertToBalIdentifier;

/**
 * Finds the Ballerina construct each flow became. It applies the converter's own naming rules
 * ({@code MuleToBalConverter}) and then checks that the name exists in the generated code; if not, the link is
 * unknown. HTTP, APIkit, scheduler and Pub/Sub flows are not linked yet.
 */
final class BallerinaLinker {

    private final Context ctx;
    private final Set<String> functionNames = new HashSet<>();
    private final List<Service> services = new ArrayList<>();

    BallerinaLinker(Context ctx, List<TextDocument> documents) {
        this.ctx = ctx;
        for (TextDocument document : documents) {
            for (Function function : document.functions()) {
                functionNames.add(function.functionName());
            }
            services.addAll(document.services());
        }
    }

    Optional<BallerinaLink> link(String flowName, Optional<MuleRecord> source) {
        if (source.isEmpty() || source.get() instanceof FileListener) {
            return Optional.of(function(convertToBalIdentifier(flowName)));
        }
        if (source.get() instanceof VMListener listener) {
            String name = ctx.projectCtx.vmQueueNameToBalFuncMap.getOrDefault(listener.queueName(),
                    convertToBalIdentifier(flowName));
            return Optional.of(function(name));
        }
        if (source.get() instanceof AnypointMqSubscriber subscriber) {
            return Optional.of(anypointMqService(subscriber));
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
            if (service.basePath().equals(serviceName) && service.listenerRefs().contains(listenerName)) {
                for (Remote remote : service.remoteFunctions()) {
                    if (remote.function().functionName().equals("onMessage")) {
                        found = true;
                    }
                }
            }
        }
        return new BallerinaLink(Construct.REMOTE_METHOD, Optional.of("onMessage"), Optional.of(serviceName),
                Optional.empty(), Optional.empty(), found);
    }
}
