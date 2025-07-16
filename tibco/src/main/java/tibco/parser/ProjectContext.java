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

package tibco.parser;

import org.jetbrains.annotations.NotNull;
import org.w3c.dom.Element;
import tibco.LoggingContext;
import tibco.TibcoToBalConverter;
import tibco.analyzer.TibcoAnalysisReport;
import tibco.analyzer.TibcoAnalysisReport.PartiallySupportedActivityElement.NamedPartiallySupportedActivityElement;
import tibco.analyzer.TibcoAnalysisReport.PartiallySupportedActivityElement.UnNamedPartiallySupportedActivityElement;
import tibco.analyzer.TibcoAnalysisReport.UnhandledActivityElement.NamedUnhandledActivityElement;
import tibco.analyzer.TibcoAnalysisReport.UnhandledActivityElement.UnNamedUnhandledActivityElement;
import tibco.converter.ConversionUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

public final class ProjectContext implements Context, LoggingContext {

    private long nextAnonProcessIndex = 0;
    private long nextAnonXSLTIndex = 0;
    private long nextUnhandledActivityIndex = 0;
    private final String projectPath;
    private final TibcoToBalConverter.ProjectConversionContext conversionContext;
    private final Set<TibcoAnalysisReport.UnhandledActivityElement> unhandledActivities = new HashSet<>();
    private final Set<TibcoAnalysisReport.PartiallySupportedActivityElement> partiallySupportedActivities =
            new HashSet<>();
    private final Set<Element> uniqueActivityElements = new HashSet<>();
    private int totalActivityCount = 0;

    public ProjectContext(TibcoToBalConverter.ProjectConversionContext cx, String projectPath) {
        assert cx != null : "Project conversion context cannot be null";
        this.projectPath = projectPath;
        conversionContext = cx;
    }

    @Override
    public void log(Level level, String message) {
        conversionContext.log(level, message);
    }

    @Override
    public void logState(String message) {
        conversionContext.logState(message);
    }

    @Override
    public String getNextAnonymousProcessName() {
        return "AnonymousProcess" + nextAnonProcessIndex++;
    }

    @Override
    public String getAnonymousXSLTName() {
        return "SharedTransform" + nextAnonXSLTIndex++;
    }

    @Override
    public String getAnonUnhandledActivityName() {
        return "unhandled" + nextUnhandledActivityIndex++;
    }

    public String getFileContent(String relativePath) throws IOException {
        Path fullPath = Paths.get(projectPath, relativePath);
        return Files.readString(fullPath);
    }

    public String projectPath() {
        return projectPath;
    }

    @Override
    public void registerUnhandledActivity(org.w3c.dom.Element element, String name, String type, String fileName) {
        if (name != null && !name.isEmpty() && type != null && !type.isEmpty()) {
            unhandledActivities.add(new NamedUnhandledActivityElement(name, type, element, fileName));
        } else {
            unhandledActivities.add(new UnNamedUnhandledActivityElement(element, fileName));
        }

        StringBuilder sb = new StringBuilder("[UNHANDLED ACTIVITY]");
        if (name != null && !name.isEmpty()) {
            sb.append(" name='").append(name).append("'");
        }
        if (type != null && !type.isEmpty()) {
            sb.append(" type='").append(type).append("'");
        }
        sb.append(" element=").append(ConversionUtils.elementToString(element));
        log(Level.SEVERE, sb.toString());
    }

    @Override
    public void registerPartiallySupportedActivity(org.w3c.dom.Element element, String name, String type) {
        if (name != null && !name.isEmpty() && type != null && !type.isEmpty()) {
            partiallySupportedActivities.add(new NamedPartiallySupportedActivityElement(name, type, element, "parser"));
        } else {
            partiallySupportedActivities.add(new UnNamedPartiallySupportedActivityElement(element, "parser"));
        }

        StringBuilder sb = new StringBuilder("[PARTIALLY SUPPORTED ACTIVITY]");
        if (name != null && !name.isEmpty()) {
            sb.append(" name='").append(name).append("'");
        }
        if (type != null && !type.isEmpty()) {
            sb.append(" type='").append(type).append("'");
        }
        sb.append(" element=").append(ConversionUtils.elementToString(element));
        log(Level.WARN, sb.toString());
    }

    @Override
    public void registerUnsupportedResource(org.w3c.dom.Element element, String name) {
        StringBuilder sb = new StringBuilder("[UNSUPPORTED RESOURCE]");
        if (name != null && !name.isEmpty()) {
            sb.append(" name='").append(name).append("'");
        }
        sb.append(" element=").append(ConversionUtils.elementToString(element));
        log(Level.SEVERE, sb.toString());
    }

    @Override
    public void registerPartiallySupportedResource(org.w3c.dom.Element element, String name) {
        StringBuilder sb = new StringBuilder("[PARTIALLY SUPPORTED RESOURCE]");
        if (name != null && !name.isEmpty()) {
            sb.append(" name='").append(name).append("'");
        }
        sb.append(" element=").append(ConversionUtils.elementToString(element));
        log(Level.WARN, sb.toString());
    }

    @Override
    public void registerUnsupportedTransition(org.w3c.dom.Element element) {
        StringBuilder sb = new StringBuilder("[UNSUPPORTED TRANSITION]");
        sb.append(" element=").append(ConversionUtils.elementToString(element));
        log(Level.SEVERE, sb.toString());
    }

    @Override
    public void registerUnsupportedSchema(org.w3c.dom.Element element) {
        StringBuilder sb = new StringBuilder("[UNSUPPORTED SCHEMA]");
        sb.append(" element=").append(ConversionUtils.elementToString(element));
        log(Level.SEVERE, sb.toString());
    }

    @Override
    public void registerPartiallySupportedSchema(org.w3c.dom.Element element) {
        StringBuilder sb = new StringBuilder("[PARTIALLY SUPPORTED SCHEMA]");
        sb.append(" element=").append(ConversionUtils.elementToString(element));
        log(Level.WARN, sb.toString());
    }

    @Override
    public void registerUnsupportedWSDLDefinition(org.w3c.dom.Element element) {
        StringBuilder sb = new StringBuilder("[UNSUPPORTED WSDL DEFINITION]");
        sb.append(" element=").append(ConversionUtils.elementToString(element));
        log(Level.SEVERE, sb.toString());
    }

    @Override
    public void registerPartiallySupportedWSDLDefinition(org.w3c.dom.Element element) {
        StringBuilder sb = new StringBuilder("[PARTIALLY SUPPORTED WSDL DEFINITION]");
        sb.append(" element=").append(ConversionUtils.elementToString(element));
        log(Level.WARN, sb.toString());
    }

    @Override
    public void incrementActivityCount(org.w3c.dom.Element element) {
        if (uniqueActivityElements.add(element)) {
            totalActivityCount++;
        }
    }

    @NotNull
    public Set<TibcoAnalysisReport.UnhandledActivityElement> getUnhandledActivities() {
        return new HashSet<>(unhandledActivities);
    }

    @NotNull
    public Set<TibcoAnalysisReport.PartiallySupportedActivityElement> getPartiallySupportedActivities() {
        return new HashSet<>(partiallySupportedActivities);
    }

    public int getTotalActivityCount() {
        return totalActivityCount;
    }
}
