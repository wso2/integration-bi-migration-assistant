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

package converter.tibco;

import ballerina.BallerinaModel;
import tibco.TibcoModel;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.STRING;

final class ResourceConvertor {

    private ResourceConvertor() {

    }

    public static void convertJDBCResource(ProjectContext cx, TibcoModel.Resource.JDBCResource resource) {
        Map<String, BallerinaModel.ModuleVar> substitutions = convertSubstitutionBindings(cx,
                resource.substitutionBindings());
        BallerinaModel.Expression.NewExpression constructorCall = new BallerinaModel.Expression.NewExpression(
                Stream.of(resource.dbUrl(), resource.userName(), resource.password())
                        .map(value -> toExpr(substitutions, value)).toList());
        BallerinaModel.ModuleVar resourceVar =
                new BallerinaModel.ModuleVar(cx.getUtilityVarName(resource.name()), "jdbc:Client",
                        new BallerinaModel.Expression.CheckPanic(constructorCall), false, false);
        cx.addResourceDeclaration(resource.name(), resourceVar, substitutions.values(), List.of(Library.JDBC));
    }

    public static void convertHttpConnectionResource(ProjectContext cx,
                                                     TibcoModel.Resource.HTTPConnectionResource resource) {
    }

    public static void convertHttpClientResource(ProjectContext cx, TibcoModel.Resource.HTTPClientResource resource) {
        Map<String, BallerinaModel.ModuleVar> substitutions = convertSubstitutionBindings(cx,
                resource.substitutionBindings());
        String hostName = hostName(resource);
        if (resource.port().isPresent()) {
            hostName = hostName + ":" + resource.port().get();
        }
        BallerinaModel.Expression.NewExpression constructorCall = new BallerinaModel.Expression.NewExpression(
                List.of(toExpr(substitutions, hostName))
        );
        BallerinaModel.ModuleVar resourceVar =
                new BallerinaModel.ModuleVar(cx.getUtilityVarName(resource.name()), "http:Client",
                        new BallerinaModel.Expression.CheckPanic(constructorCall), false, false);
        cx.addResourceDeclaration(resource.name(), resourceVar, substitutions.values(), List.of(Library.HTTP));
    }

    private static String hostName(TibcoModel.Resource.HTTPClientResource resource) {
        for (TibcoModel.Resource.SubstitutionBinding binding : resource.substitutionBindings()) {
            if (binding.template().equals("host")) {
                return binding.propName();
            }
        }
        return "localhost";
    }

    private record SubstitutionResult(boolean hasInterpolations, String result) {

    }

    private static BallerinaModel.Expression toExpr(Map<String, BallerinaModel.ModuleVar> configVars, String value) {
        var result = substituteIfNeeded(configVars, value);
        if (!result.hasInterpolations) {
            return new BallerinaModel.Expression.StringConstant(result.result);
        }
        return new BallerinaModel.Expression.StringTemplate(result.result());
    }

    private static SubstitutionResult substituteIfNeeded(Map<String, BallerinaModel.ModuleVar> configVars,
                                                         String value) {
        boolean hasInterpolations = false;
        String result = value;

        for (Map.Entry<String, BallerinaModel.ModuleVar> entry : configVars.entrySet()) {
            if (value.contains(entry.getKey())) {
                hasInterpolations = true;
                result = result.replace(entry.getKey(), "${" + entry.getValue().name() + "}");
            }
        }

        return new SubstitutionResult(hasInterpolations, result);
    }

    private static Map<String, BallerinaModel.ModuleVar> convertSubstitutionBindings(
            ProjectContext cx,
            Collection<TibcoModel.Resource.SubstitutionBinding> bindings) {
        record ConversionResult(String bindingName, BallerinaModel.ModuleVar varName) {

        }
        return bindings.stream()
                .map(each -> new ConversionResult(each.propName(), convertSubstitutionBinding(cx, each))).collect(
                        Collectors.toMap(ConversionResult::bindingName, ConversionResult::varName));
    }

    private static BallerinaModel.ModuleVar convertSubstitutionBinding(ProjectContext cx,
                                                                       TibcoModel.Resource.SubstitutionBinding binding) {
        String name = cx.getUtilityVarName(binding.template());
        return BallerinaModel.ModuleVar.configurable(name, STRING);
    }
}
