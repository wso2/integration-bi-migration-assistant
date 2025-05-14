/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

import common.BallerinaModel.Expression;
import common.BallerinaModel.Expression.CheckPanic;
import common.BallerinaModel.Expression.NewExpression;
import common.BallerinaModel.Expression.StringConstant;
import common.BallerinaModel.ModuleVar;
import common.ConversionUtils;
import tibco.TibcoModel;
import tibco.TibcoModel.Resource.HTTPClientResource;
import tibco.TibcoModel.Resource.HTTPConnectionResource;
import tibco.TibcoModel.Resource.JDBCResource;

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
        Map<String, ModuleVar> substitutions = convertSubstitutionBindings(cx, resource.substitutionBindings());
        NewExpression constructorCall = new NewExpression(
                Stream.of(resource.dbUrl(), resource.userName(), resource.password())
                        .map(value -> toExpr(substitutions, value))
                        .toList());
        ModuleVar resourceVar =
                new ModuleVar(cx.getUtilityVarName(resource.name()), "jdbc:Client",
                        Optional.of(new CheckPanic(constructorCall)), false, false);
        cx.addResourceDeclaration(resource.name(), resourceVar, substitutions.values(), List.of(Library.JDBC));
    }

    public static void convertHttpConnectionResource(ProjectContext cx, HTTPConnectionResource resource) {
    }

    public static void convertHttpClientResource(ProjectContext cx, HTTPClientResource resource) {
        Map<String, ModuleVar> substitutions = convertSubstitutionBindings(cx, resource.substitutionBindings());
        String hostName = hostName(resource);
        if (resource.port().isPresent()) {
            hostName = hostName + ":" + resource.port().get();
        }
        NewExpression constructorCall = new NewExpression(List.of(toExpr(substitutions, hostName)));
        ModuleVar resourceVar =
                new ModuleVar(cx.getUtilityVarName(resource.name()), "http:Client",
                        Optional.of(new CheckPanic(constructorCall)), false, false);
        cx.addResourceDeclaration(resource.name(), resourceVar, substitutions.values(), List.of(Library.HTTP));
    }

    private static String hostName(HTTPClientResource resource) {
        for (TibcoModel.Resource.SubstitutionBinding binding : resource.substitutionBindings()) {
            if (binding.template().equals("host")) {
                return binding.propName();
            }
        }
        return "localhost";
    }

    public static void convertHttpSharedResource(ProjectContext cx, TibcoModel.Resource.HTTPSharedResource resource) {
        // public listener http:Listener creditapp_module_MainProcess_listener = new (8082, {host: "localhost"});
        Expression.BallerinaExpression init = ConversionUtils.exprFrom(
                "checkpanic new (%d, {host: \"%s\"})".formatted(resource.port(), resource.host()));
        ModuleVar resourceVar =
                new ModuleVar(tibco.converter.ConversionUtils.sanitizes(resource.name()), "listener http:Listener",
                        Optional.of(init), false, false);
        cx.addResourceDeclaration(resource.name(), resourceVar, List.of(), List.of(Library.HTTP));
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
            ProjectContext cx, Collection<TibcoModel.Resource.SubstitutionBinding> bindings) {
        record ConversionResult(String bindingName, ModuleVar varName) {

        }
        return bindings.stream()
                .map(each -> new ConversionResult(each.propName(), convertSubstitutionBinding(cx, each)))
                .collect(Collectors.toMap(ConversionResult::bindingName, ConversionResult::varName));
    }

    private static ModuleVar convertSubstitutionBinding(ProjectContext cx,
                                                        TibcoModel.Resource.SubstitutionBinding binding) {
        return ModuleVar.configurable(cx.getUtilityVarName(binding.template()), STRING);
    }
}
