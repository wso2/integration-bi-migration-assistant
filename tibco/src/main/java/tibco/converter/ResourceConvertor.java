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
import tibco.LoggingContext;
import tibco.TibcoToBalConverter;
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

import static common.BallerinaModel.TypeDesc.BuiltinType.STRING;

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
            ModuleVar resourceVar = new ModuleVar(cx.getUtilityVarName(resource.name()), "jdbc:Client",
                    Optional.of(new CheckPanic(constructorCall)), false, false);
            cx.addResourceDeclaration(resource.name(), resourceVar, substitutions.values(), List.of(Library.JDBC));
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
            ModuleVar resourceVar = new ModuleVar(cx.getUtilityVarName(resource.name()), "http:Client",
                    Optional.of(new CheckPanic(constructorCall)), false, false);
            cx.addResourceDeclaration(resource.name(), resourceVar, substitutions.values(), List.of(Library.HTTP));
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
            String name = tibco.converter.ConversionUtils.sanitizes(resource.name());
            BallerinaModel.Listener listener = new BallerinaModel.Listener.HTTPListener(name,
                    Integer.toString(resource.port()), resource.host());
            cx.addListnerDeclartion(resource.name(), listener, List.of(), List.of(Library.HTTP));
        } catch (Exception e) {
            cx.registerResourceConversionFailure(resource);
        }
    }

    public static void convertJDBCSharedResource(ProjectContext cx, Resource.JDBCSharedResource resource) {
        try {
            NewExpression constructorCall = new NewExpression(List.of(new StringConstant(resource.location())));
            ModuleVar resourceVar = new ModuleVar(cx.getUtilityVarName(resource.name()), "jdbc:Client",
                    Optional.of(new CheckPanic(constructorCall)), false, false);
            cx.addResourceDeclaration(resource.name(), resourceVar, List.of(), List.of(Library.JDBC));
            cx.addJavaDependency(pickJavaSQLConnector(cx, resource.location()));
        } catch (Exception e) {
            cx.registerResourceConversionFailure(resource);
        }
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
}
