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

import common.BallerinaModel;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import tibco.TibcoModel;
import tibco.TibcoToBalConverter;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ProjectConverter {
    public static ConversionResult convertProject(
            TibcoToBalConverter.ProjectConversionContext conversionContext, Collection<TibcoModel.Process> processes,
            Collection<TibcoModel.Type.Schema> types, Collection<TibcoModel.Resource.JDBCResource> jdbcResources,
            Collection<TibcoModel.Resource.HTTPConnectionResource> httpConnectionResources,
            Set<TibcoModel.Resource.HTTPClientResource> httpClientResources) {
        ProjectContext cx = new ProjectContext(conversionContext);
        convertResources(cx, jdbcResources, httpConnectionResources, httpClientResources);

        record ProcessResult(TibcoModel.Process process, ProcessConverter.TypeConversionResult result) {

        }
        List<ProcessResult> results =
                processes.stream()
                        .map(process -> new ProcessResult(process,
                                ProcessConverter.convertTypes(cx.getProcessContext(process), process)))
                        .toList();
        List<TibcoModel.Type.Schema> schemas = new ArrayList<>(types);
        for (TibcoModel.Process each : processes) {
            accumSchemas(each, schemas);
        }
        SyntaxTree typeSyntaxTree = convertTypes(cx, schemas);
        // We need to ensure all the type definitions have been processed before we
        // start processing the functions
        List<BallerinaModel.TextDocument> textDocuments = results.stream()
                .map(result -> {
                    TibcoModel.Process process = result.process();
                    return ProcessConverter.convertBody(cx.getProcessContext(process), process, result.result());
                }).toList();
        return new ConversionResult(cx.serialize(textDocuments), typeSyntaxTree);
    }

    private static void accumSchemas(TibcoModel.Process process, Collection<TibcoModel.Type.Schema> accum) {
        for (TibcoModel.Type each : process.types()) {
            if (each instanceof TibcoModel.Type.Schema schema) {
                accum.add(schema);
            }
        }
    }

    private static void convertResources(ProjectContext cx, Collection<TibcoModel.Resource.JDBCResource> jdbcResources,
                                         Collection<TibcoModel.Resource.HTTPConnectionResource> httpConnectionResources,
                                         Set<TibcoModel.Resource.HTTPClientResource> httpClientResources) {
        for (TibcoModel.Resource.JDBCResource resource : jdbcResources) {
            ResourceConvertor.convertJDBCResource(cx, resource);
        }
        for (TibcoModel.Resource.HTTPConnectionResource resource : httpConnectionResources) {
            ResourceConvertor.convertHttpConnectionResource(cx, resource);
        }
        for (TibcoModel.Resource.HTTPClientResource resource : httpClientResources) {
            ResourceConvertor.convertHttpClientResource(cx, resource);
        }
    }

    static SyntaxTree convertTypes(ProjectContext cx, Collection<TibcoModel.Type.Schema> schemas) {
        ContextWithFile typeContext = cx.getTypeContext();
        return TypeConverter.convertSchemas(typeContext, schemas);
    }
}
