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
import common.BallerinaModel.Expression.MappingConstructor;
import common.BallerinaModel.Expression.MappingConstructor.MappingField;
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
import tibco.model.Resource.SFTPResource;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static common.BallerinaModel.TypeDesc.BuiltinType.INT;
import static common.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static tibco.converter.Library.JMS;

final class ResourceConvertor {

    private static final int DEFAULT_SFTP_PORT = 22;

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

    public static void convertSftpResource(ProjectContext cx, SFTPResource resource) {
        try {
            String clientName = cx.getUtilityVarName(resource.name().isEmpty()
                    ? ConversionUtils.resourceNameFromPath(resource.path()) : resource.name());
            Map<String, ModuleVar> configurables = new LinkedHashMap<>();
            Expression host = sftpConfigValue(cx, resource, clientName, "host", STRING, Optional.empty(),
                    configurables);
            Expression port = sftpConfigValue(cx, resource, clientName, "port", INT,
                    Optional.of(new Expression.IntConstant(DEFAULT_SFTP_PORT)), configurables);
            MappingField username = new MappingField("username",
                    sftpConfigValue(cx, resource, clientName, "userName", STRING, Optional.empty(), configurables));
            List<MappingField> auth = resource.privKeyAuth()
                    ? List.of(
                            new MappingField("credentials", new MappingConstructor(List.of(username))),
                            new MappingField("privateKey", new MappingConstructor(List.of(
                                    new MappingField("path", sftpConfigValue(cx, resource, clientName, "privKey",
                                            STRING, Optional.empty(), configurables)),
                                    new MappingField("password", sftpSecretValue(cx, resource, clientName,
                                            "privKeypassword", configurables))))))
                    : List.of(new MappingField("credentials", new MappingConstructor(List.of(username,
                            new MappingField("password",
                                    sftpSecretValue(cx, resource, clientName, "password", configurables))))));
            MappingConstructor clientConfig = new MappingConstructor(List.of(
                    new MappingField("protocol", new Expression.VariableReference("ftp:SFTP")),
                    new MappingField("host", host),
                    new MappingField("port", port),
                    new MappingField("auth", new MappingConstructor(auth))));
            ModuleVar resourceVar = new ModuleVar(clientName, "ftp:Client",
                    Optional.of(new CheckPanic(new NewExpression(List.of(clientConfig)))), false, false);
            cx.addResourceDeclaration(resource.path(), resourceVar, configurables.values(), List.of(Library.FTP));
        } catch (Exception e) {
            cx.registerResourceConversionFailure(resource);
        }
    }

    // Secrets always become configurables, even when the resource file carries a literal, to keep them out of the
    // generated source.
    private static Expression sftpSecretValue(ProjectContext cx, SFTPResource resource, String clientName,
                                              String field, Map<String, ModuleVar> configurables) {
        return sftpBindingName(resource, field)
                .map(bindingName -> sftpModuleProperty(cx, bindingName, STRING))
                .orElseGet(() -> sftpConfigurable(cx, clientName + "_" + field, STRING, configurables));
    }

    private static Expression sftpConfigValue(ProjectContext cx, SFTPResource resource, String clientName,
                                              String field, BallerinaModel.TypeDesc type,
                                              Optional<Expression> defaultValue,
                                              Map<String, ModuleVar> configurables) {
        Optional<String> bindingName = sftpBindingName(resource, field);
        if (bindingName.isPresent()) {
            return sftpModuleProperty(cx, bindingName.get(), type);
        }
        Optional<String> literal = Optional.ofNullable(resource.configuration().get(field));
        if (literal.isPresent()) {
            return type == INT
                    ? new Expression.IntConstant(Integer.parseInt(literal.get().trim()))
                    : new StringConstant(ConversionUtils.escapeString(literal.get()));
        }
        return defaultValue.orElseGet(() -> sftpConfigurable(cx, clientName + "_" + field, type, configurables));
    }

    private static Optional<String> sftpBindingName(SFTPResource resource, String field) {
        return resource.substitutionBindings().stream()
                .filter(binding -> binding.template().equals(field))
                .map(Resource.SubstitutionBinding::propName)
                .findFirst();
    }

    private static Expression sftpModuleProperty(ProjectContext cx, String propName, BallerinaModel.TypeDesc type) {
        return new Expression.VariableReference(cx.getOrAddConfigurableVariable(propName, type));
    }

    private static Expression sftpConfigurable(ProjectContext cx, String name, BallerinaModel.TypeDesc type,
                                               Map<String, ModuleVar> configurables) {
        ModuleVar configurable = ModuleVar.configurable(cx.getUtilityVarName(name), type);
        configurables.put(configurable.name(), configurable);
        return new Expression.VariableReference(configurable.name());
    }

    public static void convertHttpSharedResource(ProjectContext cx, Resource.HTTPSharedResource resource) {
        try {
            String listenerName = ConversionUtils.sanitizes(
                    ConversionUtils.resourceNameFromPath(resource.path()));
            Expression port = getOptionalConfigurableValue(cx, resource.port(), listenerName + "Port", INT,
                    value -> common.ConversionUtils.exprFrom(Integer.toString(value)));
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

    @NotNull
    static BallerinaModel.Listener.JMSListener convertJMSSharedResource(
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
        return getOptionalConfigurableValue(cx, configValue, onMissingName, STRING, StringConstant::new);
    }

    private static <T> @NotNull Expression getOptionalConfigurableValue(ProjectContext cx, Optional<T> configValue,
            String onMissingName, BallerinaModel.TypeDesc type, Function<T, Expression> toExpr) {
        return configValue.map(toExpr).orElseGet(() ->
                new Expression.VariableReference(cx.addConfigurableVariable(onMissingName, onMissingName, type))
        );
    }
}
