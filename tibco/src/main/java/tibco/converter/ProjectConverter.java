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
import tibco.TibcoToBalConverter;
import tibco.analyzer.AnalysisResult;
import tibco.analyzer.TibcoAnalysisReport;
import tibco.model.Process;
import tibco.model.Process5;
import tibco.model.Process6;
import tibco.model.Resource;
import tibco.model.Type;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.logging.Logger;

public class ProjectConverter {

    public record ProjectResources(
            Collection<Resource.JDBCResource> jdbcResources,
            Collection<Resource.HTTPConnectionResource> httpConnectionResources,
            Set<Resource.HTTPClientResource> httpClientResources,
            Set<Resource.HTTPSharedResource> httpSharedResources,
            Set<Resource.JDBCSharedResource> jdbcSharedResource,
            Set<Resource.JMSSharedResource> jmsSharedResource,
            Set<Resource.SharedVariable> sharedVariables
    ) {

    }

    public static ConversionResult convertProject(
            TibcoToBalConverter.ProjectConversionContext conversionContext,
            Map<Process, AnalysisResult> analysisResult, Collection<Process> processes, Collection<Type.Schema> types,
            ProjectResources projectResources, TibcoAnalysisReport report) {
        ProjectContext cx = new ProjectContext(conversionContext, analysisResult);
        convertResources(cx, projectResources);

        List<ProcessResult> results =
                processes.stream()
                        .map(process -> convertServices(cx, process, projectResources))
                        .toList();
        List<Type.Schema> schemas = new ArrayList<>(types);
        for (Process each : processes) {
            if (each instanceof Process6 process6) {
                accumSchemas(process6, schemas);
            }
        }
        List<BallerinaModel.TextDocument> textDocuments = results.stream()
                .map(result -> convertBody(result, result.process(), cx)).toList();
        schemas.addAll(cx.getXSDSchemas());
        SyntaxTree typeSyntaxTree = convertTypes(cx, schemas);
        return new ConversionResult(cx.serialize(textDocuments), typeSyntaxTree, report);
    }

    private static BallerinaModel.TextDocument convertBody(ProcessResult result, Process process,
                                                           ProjectContext cx) {
        return switch (process) {
            case Process5 process5 ->
                    ProcessConverter.convertBody(cx.getProcessContext(process), process5, result.result());
            case Process6 process6 ->
                    ProcessConverter.convertBody(cx.getProcessContext(process), process6, result.result());
        };
    }

    record ProcessResult(Process process, ProcessConverter.TypeConversionResult result) {

    }

    private static ProcessResult convertServices(ProjectContext cx, Process process,
                                                 ProjectResources projectResources) {

        return switch (process) {
            case Process5 process5 -> convertServices(cx, process5, projectResources);
            case Process6 process6 -> convertServices(cx, process6);
        };
    }

    private static ProcessResult convertServices(ProjectContext cx, Process5 process,
                                                 ProjectResources projectResources) {
        List<BallerinaModel.Service> startService;
        Optional<BallerinaModel.Function> mainFn;
        if (isLifecycleProcess(process)) {
            startService = List.of();
            mainFn = Optional.of(ProcessConverter.createMainFunction(cx.getProcessContext(process), process));
        } else {
            startService =
                    List.of(ProcessConverter.convertStartActivityService(cx.getProcessContext(process),
                            process.transitionGroup()));
            mainFn = Optional.empty();
        }
        ProcessConverter.addProcessClient(cx.getProcessContext(process), process.transitionGroup(),
                projectResources.httpSharedResources);
        return new ProcessResult(process, new ProcessConverter.TypeConversionResult(startService, mainFn));
    }

    private static boolean isLifecycleProcess(Process5 process) {
        return process.transitionGroup()
                .startActivity() instanceof Process5.ExplicitTransitionGroup.InlineActivity.OnStartupEventSource;
    }

    private static ProcessResult convertServices(ProjectContext cx, Process6 process) {
        return new ProcessResult(process, ProcessConverter.convertTypes(cx.getProcessContext(process), process));
    }

    private static void accumSchemas(Process6 process, Collection<Type.Schema> accum) {
        for (Type each : process.types()) {
            if (each instanceof Type.Schema schema) {
                accum.add(schema);
            }
        }
    }

    private static void convertResources(ProjectContext cx, ProjectResources projectResources) {
        for (Resource.JDBCResource resource : projectResources.jdbcResources) {
            ResourceConvertor.convertJDBCResource(cx, resource);
        }
        for (Resource.HTTPConnectionResource resource : projectResources.httpConnectionResources) {
            ResourceConvertor.convertHttpConnectionResource(cx, resource);
        }
        for (Resource.HTTPClientResource resource : projectResources.httpClientResources) {
            ResourceConvertor.convertHttpClientResource(cx, resource);
        }
        for (Resource.HTTPSharedResource resource : projectResources.httpSharedResources) {
            ResourceConvertor.convertHttpSharedResource(cx, resource);
        }
        for (Resource.JDBCSharedResource resource : projectResources.jdbcSharedResource) {
            ResourceConvertor.convertJDBCSharedResource(cx, resource);
        }
        for (Resource.JMSSharedResource resource : projectResources.jmsSharedResource) {
            cx.addJMSResource(resource);
        }
        for (Resource.SharedVariable resource : projectResources.sharedVariables) {
            cx.addSharedVariable(resource);
        }
    }

    static SyntaxTree convertTypes(ProjectContext cx, Collection<Type.Schema> schemas) {
        ContextWithFile typeContext = cx.getTypeContext();
        return TypeConverter.convertSchemas(typeContext, schemas);
    }

    public static Logger logger() {
        return TibcoToBalConverter.logger();
    }
}
