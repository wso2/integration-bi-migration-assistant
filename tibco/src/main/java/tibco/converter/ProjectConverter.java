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
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import tibco.Process;
import tibco.TibcoModel;
import tibco.TibcoToBalConverter;
import tibco.analyzer.AnalysisResult;
import tibco.analyzer.TibcoAnalysisReport;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import java.util.stream.Stream;

public class ProjectConverter {

    public static ConversionResult convertProject(
            TibcoToBalConverter.ProjectConversionContext conversionContext,
            Map<Process, AnalysisResult> analysisResult,
            Collection<Process> processes, Collection<TibcoModel.Type.Schema> types,
            Collection<TibcoModel.Resource.JDBCResource> jdbcResources,
            Collection<TibcoModel.Resource.HTTPConnectionResource> httpConnectionResources,
            Set<TibcoModel.Resource.HTTPClientResource> httpClientResources,
            Set<TibcoModel.Resource.HTTPSharedResource> httpSharedResources,
            Set<TibcoModel.Resource.JDBCSharedResource> jdbcSharedResource, TibcoAnalysisReport report) {
        ProjectContext cx = new ProjectContext(conversionContext, analysisResult);
        convertResources(cx, jdbcResources, httpConnectionResources, httpClientResources, httpSharedResources,
                jdbcSharedResource);

        record ProcessResult(Process process, ProcessConverter.TypeConversionResult result) {

        }
        List<ProcessResult> results5 =
                processes.stream()
                        .filter(each -> each instanceof TibcoModel.Process5)
                        .map(each -> (TibcoModel.Process5) each)
                        .map(process -> {
                            BallerinaModel.Service startService =
                                    ProcessConverter.convertStartActivityService(cx.getProcessContext(process),
                                            process.transitionGroup());
                            ProcessConverter.addProcessClient(cx.getProcessContext(process), process.transitionGroup(),
                                    httpSharedResources);
                            return new ProcessResult(process, new ProcessConverter.TypeConversionResult(
                                    Stream.of(startService).toList()));
                        })
                        .toList();
        List<ProcessResult> results6 =
                processes.stream()
                        .filter(each -> each instanceof TibcoModel.Process6)
                        .map(each -> (TibcoModel.Process6) each)
                        .map(process -> new ProcessResult(process,
                                ProcessConverter.convertTypes(cx.getProcessContext(process), process)))
                        .toList();
        List<ProcessResult> results = Stream.concat(results5.stream(), results6.stream()).toList();
        List<TibcoModel.Type.Schema> schemas = new ArrayList<>(types);
        for (Process each : processes) {
            if (each instanceof TibcoModel.Process6 process6) {
                accumSchemas(process6, schemas);
            }
        }
        List<BallerinaModel.TextDocument> textDocuments = results.stream()
                .map(result -> {
                    Process process = result.process();
                    return switch (process) {
                        case TibcoModel.Process5 process5 ->
                                ProcessConverter.convertBody(cx.getProcessContext(process), process5, result.result());
                        case TibcoModel.Process6 process6 ->
                                ProcessConverter.convertBody(cx.getProcessContext(process), process6, result.result());
                    };
                }).toList();
        schemas.addAll(cx.getXSDSchemas());
        SyntaxTree typeSyntaxTree = convertTypes(cx, schemas);
        return new ConversionResult(cx.serialize(textDocuments), typeSyntaxTree, report);
    }

    private static void accumSchemas(TibcoModel.Process6 process, Collection<TibcoModel.Type.Schema> accum) {
        for (TibcoModel.Type each : process.types()) {
            if (each instanceof TibcoModel.Type.Schema schema) {
                accum.add(schema);
            }
        }
    }

    private static void convertResources(ProjectContext cx, Collection<TibcoModel.Resource.JDBCResource> jdbcResources,
                                         Collection<TibcoModel.Resource.HTTPConnectionResource> httpConnectionResources,
                                         Set<TibcoModel.Resource.HTTPClientResource> httpClientResources,
                                         Set<TibcoModel.Resource.HTTPSharedResource> httpSharedResources,
                                         Set<TibcoModel.Resource.JDBCSharedResource> jdbcSharedResource) {
        for (TibcoModel.Resource.JDBCResource resource : jdbcResources) {
            ResourceConvertor.convertJDBCResource(cx, resource);
        }
        for (TibcoModel.Resource.HTTPConnectionResource resource : httpConnectionResources) {
            ResourceConvertor.convertHttpConnectionResource(cx, resource);
        }
        for (TibcoModel.Resource.HTTPClientResource resource : httpClientResources) {
            ResourceConvertor.convertHttpClientResource(cx, resource);
        }
        for (TibcoModel.Resource.HTTPSharedResource resource : httpSharedResources) {
            ResourceConvertor.convertHttpSharedResource(cx, resource);
        }
        for (TibcoModel.Resource.JDBCSharedResource resource : jdbcSharedResource) {
            ResourceConvertor.convertJDBCSharedResource(cx, resource);
        }
    }

    static SyntaxTree convertTypes(ProjectContext cx, Collection<TibcoModel.Type.Schema> schemas) {
        ContextWithFile typeContext = cx.getTypeContext();
        return TypeConverter.convertSchemas(typeContext, schemas);
    }

    public static Logger logger() {
        return TibcoToBalConverter.logger();
    }
}
