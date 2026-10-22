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

package tibco.analyzer;

import io.ballerina.xsd.core.response.NodeResponse;
import org.jetbrains.annotations.NotNull;
import tibco.converter.ProjectConverter;
import tibco.converter.TypeConverter;
import tibco.model.Process;
import tibco.model.Process5;
import tibco.model.Type;
import tibco.model.XSD;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

import javax.xml.parsers.ParserConfigurationException;

public class ModelAnalyser {

    private final List<AnalysisPass> passes;

    public ModelAnalyser(List<AnalysisPass> passes) {
        this.passes = passes;
    }

    public @NotNull Map<Process, AnalysisResult> analyseProject(ProjectAnalysisContext context,
                                                               Collection<Process> processes,
                                                       Collection<Type.Schema> schemas,
                                                       ProjectConverter.ProjectResources resources) {
        // Every schema must be registered before any activity function is named, otherwise an activity in a
        // process analysed early can take a name that a schema of a later process declares as a type.
        List<Type.Schema> processSchemas = ProjectConverter.collectSchemas(schemas, processes);
        List<Type.Schema> activitySchemas = processes.stream().flatMap(ModelAnalyser::jsonParserSchemas).toList();
        List<Type.Schema> moduleSchemas = Stream.concat(processSchemas.stream(), activitySchemas.stream()).toList();
        moduleSchemas.stream().map(Type.Schema::xsdTypes).flatMap(Collection::stream).forEachOrdered(each -> {
            context.addXsdType(each.name(), each.type());
        });
        reserveGeneratedTypeNames(context, moduleSchemas, activitySchemas.isEmpty());
        return analyseProcesses(context, processes);
    }

    private static void reserveGeneratedTypeNames(ProjectAnalysisContext context, List<Type.Schema> moduleSchemas,
                                                  boolean conversionUsesSameSchemas) {
        NodeResponse generatedTypes;
        try {
            generatedTypes = TypeConverter.generateTypes(moduleSchemas);
        } catch (RuntimeException e) {
            // Type conversion reports this failure itself when it regenerates the types.
            return;
        }
        context.reserveTypeNames(TypeConverter.declaredNames(generatedTypes));
        if (conversionUsesSameSchemas) {
            context.setReusableGeneratedTypes(generatedTypes);
        }
    }

    private static Stream<Type.Schema> jsonParserSchemas(Process process) {
        return process instanceof Process5 process5 ? jsonParserSchemas(process5.transitionGroup()) : Stream.empty();
    }

    private static Stream<Type.Schema> jsonParserSchemas(Process5.ExplicitTransitionGroup group) {
        return group.activities().stream().flatMap(activity -> switch (activity) {
            case Process5.ExplicitTransitionGroup.InlineActivity.JSONParser parser ->
                    parser.targetType().stream().flatMap(ModelAnalyser::toSchema);
            case Process5.ExplicitTransitionGroup.InlineActivityWithBody withBody ->
                    jsonParserSchemas(withBody.body());
            default -> Stream.empty();
        });
    }

    private static Stream<Type.Schema> toSchema(XSD xsd) {
        try {
            return Stream.of(xsd.toSchema());
        } catch (ParserConfigurationException e) {
            return Stream.empty();
        }
    }

    private Map<Process, AnalysisResult> analyseProcesses(ProjectAnalysisContext cx,
                                                          Collection<Process> processes) {
        // Add all processes to the queue at the beginning
        cx.addProcessesToQueue(processes);

        Map<Process, AnalysisResult> analysisResults = new HashMap<>();

        // Pull processes from the queue and analyze them
        while (cx.hasMoreProcesses()) {
            Process process = cx.getNextProcess();
            AnalysisResult combined = AnalysisResult.empty();
            for (AnalysisPass pass : passes) {
                ProcessAnalysisContext analysisContext = new ProcessAnalysisContext(cx);
                pass.analyseProcess(analysisContext, process);
                AnalysisResult result = pass.getResult(analysisContext, process);
                combined = combined.combine(result);
            }
            analysisResults.put(process, combined);
        }

        return Collections.unmodifiableMap(analysisResults);
    }

}
