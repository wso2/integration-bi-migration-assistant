/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
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

package tibco.converter;

import common.BallerinaModel;
import common.BallerinaModel.Expression;
import common.BallerinaModel.Expression.CheckPanic;
import common.BallerinaModel.Expression.NewExpression;
import common.BallerinaModel.Expression.StringConstant;
import common.BallerinaModel.ModuleVar;
import common.LoggingUtils;
import org.jetbrains.annotations.NotNull;
import tibco.LoggingContext;
import tibco.TibcoToBalConverter;
import tibco.model.Process5.ExplicitTransitionGroup.InlineActivity.JMSQueueEventSource;
import tibco.model.Resource;
import tibco.model.Resource.HTTPClientResource;
import tibco.model.Resource.HTTPConnectionResource;
import tibco.model.Resource.JDBCResource;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static common.BallerinaModel.TypeDesc.BuiltinType.INT;
import static common.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static tibco.converter.Library.JMS;

final class ResourceConvertor {

    private ResourceConvertor() {

    }

    public static void convertJDBCResource(ProjectContext cx, JDBCResource resource) {
        try {
            Map<String, ModuleVar> substitutions = convertSubstitutionBindings(cx, resource.substitutionBindings());
            NewExpression constructorCall = new NewExpression(
                    Stream.of(resource.dbUrl(), resource.userName(), resource.password())
                            .map(value -> toExpr(substitutions, value))
                            .toList());
            ModuleVar resourceVar = new ModuleVar(cx.getUtilityVarName(
                    ConversionUtils.resourceNameFromPath(resource.path())), "jdbc:Client",
                    Optional.of(new CheckPanic(constructorCall)), false, false);
            cx.addResourceDeclaration(resource.path(), resourceVar, substitutions.values(), List.of(Library.JDBC));
            cx.addJavaDependency(pickJavaSQLConnector(cx, resource.dbUrl()));
        } catch (Exception e) {
            cx.registerResourceConversionFailure(resource);
        }
    }

    private static TibcoToBalConverter.JavaDependencies pickJavaSQLConnector(LoggingContext cx, String dbUrl) {
        String url = dbUrl.trim();
        if (url.startsWith("jdbc:h2:")) {
            return TibcoToBalConverter.JavaDependencies.JDBC_H2;
        } else if (url.startsWith("jdbc:mysql:")) {
            return TibcoToBalConverter.JavaDependencies.JDBC_MYSQL;
        } else if (url.startsWith("jdbc:postgresql:")) {
            return TibcoToBalConverter.JavaDependencies.JDBC_POSTGRESQL;
        } else if (url.startsWith("jdbc:oracle:")) {
            return TibcoToBalConverter.JavaDependencies.JDBC_ORACLE;
        } else if (url.startsWith("jdbc:mariadb:")) {
            return TibcoToBalConverter.JavaDependencies.JDBC_MARIADB;
        }
        // Default to H2 for unknown JDBC URLs
        cx.log(LoggingUtils.Level.WARN, "Unknown JDBC URL format: " + url + ". Defaulting to H2 connector.");
        return TibcoToBalConverter.JavaDependencies.JDBC_H2;
    }

    public static void convertHttpConnectionResource(ProjectContext cx, HTTPConnectionResource resource) {
    }

    public static void convertHttpClientResource(ProjectContext cx, HTTPClientResource resource) {
        try {
            Map<String, ModuleVar> substitutions = convertSubstitutionBindings(cx, resource.substitutionBindings());
            String hostName = hostName(resource);
            if (resource.port().isPresent()) {
                hostName = hostName + ":" + resource.port().get();
            }
            NewExpression constructorCall = new NewExpression(List.of(toExpr(substitutions, hostName)));
            ModuleVar resourceVar = new ModuleVar(cx.getUtilityVarName(
                    ConversionUtils.resourceNameFromPath(resource.path())), "http:Client",
                    Optional.of(new CheckPanic(constructorCall)), false, false);
            cx.addResourceDeclaration(resource.path(), resourceVar, substitutions.values(), List.of(Library.HTTP));
        } catch (Exception e) {
            cx.registerResourceConversionFailure(resource);
        }
    }

    private static String hostName(HTTPClientResource resource) {
        for (Resource.SubstitutionBinding binding : resource.substitutionBindings()) {
            if (binding.template().equals("host")) {
                return binding.propName();
            }
        }
        return "localhost";
    }

    public static void convertHttpSharedResource(ProjectContext cx, Resource.HTTPSharedResource resource) {
        try {
            String listenerName = ConversionUtils.sanitizes(
                    ConversionUtils.resourceNameFromPath(resource.path()));
            Expression port =
                    resource.port().map(value -> (Expression) common.ConversionUtils.exprFrom(Integer.toString(value)))
                            .orElseGet(() -> {
                                        String onMissingName = listenerName + "Port";
                                        cx.addConfigurableVariable(onMissingName, onMissingName, INT);
                                        return new Expression.VariableReference(onMissingName);
                                    }
                            );
            BallerinaModel.Listener listener = new BallerinaModel.Listener.HTTPListener(listenerName,
                    port, Optional.of(getOptionalConfigurableValueString(cx, resource.host(), listenerName + "Host")));
            cx.addListnerDeclartion(resource.path(), listener, List.of(), List.of(Library.HTTP));
        } catch (Exception e) {
            cx.registerResourceConversionFailure(resource);
        }
    }

    public static void convertJDBCSharedResource(ProjectContext cx, Resource.JDBCSharedResource resource) {
        try {
            String clientName = cx.getUtilityVarName(ConversionUtils.resourceNameFromPath(resource.path()));
            NewExpression constructorCall = new NewExpression(
                    List.of(getOptionalConfigurableValueString(cx, resource.location(), clientName + "Location")));
            ModuleVar resourceVar = new ModuleVar(clientName, "jdbc:Client",
                    Optional.of(new CheckPanic(constructorCall)), false, false);
            cx.addResourceDeclaration(resource.path(), resourceVar, List.of(), List.of(Library.JDBC));
            cx.addJavaDependency(
                    resource.location().map(location -> pickJavaSQLConnector(cx, location)).orElseGet(() -> {
                        cx.log(LoggingUtils.Level.WARN, "JDBC url not given. Defaulting to H2 connector.");
                        return TibcoToBalConverter.JavaDependencies.JDBC_H2;
                    }));
        } catch (Exception e) {
            cx.registerResourceConversionFailure(resource);
        }
    }

    static BallerinaModel.Listener.@NotNull JMSListener convertJMSSharedResource(
            ProjectContext cx, JMSQueueEventSource jmsQueueEventSource, Resource.JMSSharedResource jmsResource,
            String listenerName, String connectionReference) {
        Expression initialContextFactory =
                getOptionalConfigurableValueString(cx, jmsResource.namingEnvironment().flatMap(
                                Resource.JMSSharedResource.NamingEnvironment::namingInitialContextFactory),
                        listenerName + "InitialContextFactory");
        Expression providerUrl = getOptionalConfigurableValueString(cx,
                jmsResource.namingEnvironment().flatMap(Resource.JMSSharedResource.NamingEnvironment::providerURL),
                listenerName + "ProviderURL");
        String destinationName = jmsQueueEventSource.sessionAttributes().destination().orElse("Default queue");

        BallerinaModel.Listener.JMSListener listener =
                new BallerinaModel.Listener.JMSListener(listenerName, initialContextFactory, providerUrl,
                        destinationName, jmsResource.connectionAttributes().flatMap(
                        Resource.JMSSharedResource.ConnectionAttributes::username),
                        jmsResource.connectionAttributes()
                                .flatMap(Resource.JMSSharedResource.ConnectionAttributes::password));
        cx.addListnerDeclartion(connectionReference, listener, List.of(),
                List.of(JMS));
        return listener;
    }

    private record SubstitutionResult(boolean hasInterpolations, String result) {

    }

    private static Expression toExpr(Map<String, ModuleVar> configVars, String value) {
        var result = substituteIfNeeded(configVars, value);
        if (!result.hasInterpolations) {
            return new StringConstant(result.result);
        }
        return new Expression.StringTemplate(result.result());
    }

    private static SubstitutionResult substituteIfNeeded(Map<String, ModuleVar> configVars, String value) {
        boolean hasInterpolations = false;
        String result = value;

        for (Map.Entry<String, ModuleVar> entry : configVars.entrySet()) {
            if (value.contains(entry.getKey())) {
                hasInterpolations = true;
                result = result.replace(entry.getKey(), "${" + entry.getValue().name() + "}");
            }
        }

        return new SubstitutionResult(hasInterpolations, result);
    }

    private static Map<String, ModuleVar> convertSubstitutionBindings(
            ProjectContext cx, Collection<Resource.SubstitutionBinding> bindings) {
        record ConversionResult(String bindingName, ModuleVar varName) {

        }
        return bindings.stream()
                .map(each -> new ConversionResult(each.propName(), convertSubstitutionBinding(cx, each)))
                .collect(Collectors.toMap(ConversionResult::bindingName, ConversionResult::varName));
    }

    private static ModuleVar convertSubstitutionBinding(ProjectContext cx,
                                                        Resource.SubstitutionBinding binding) {
        return ModuleVar.configurable(cx.getUtilityVarName(binding.template()), STRING);
    }

    private static Expression getOptionalConfigurableValueString(ProjectContext cx, Optional<String> configValue,
                                                                 String onMissingName) {
        return configValue.map(value -> (Expression) new StringConstant(value)).orElseGet(() -> {
                    cx.addConfigurableVariable(onMissingName, onMissingName, STRING);
                    return new Expression.VariableReference(onMissingName);
                }
        );
    }
}
