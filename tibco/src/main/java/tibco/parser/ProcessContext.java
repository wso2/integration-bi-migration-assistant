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

import tibco.model.NameSpace;

import java.io.IOException;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;

public class ProcessContext implements Context {

    private long nextAnonXSLTIndex = 0;
    private final ProjectContext projectContext;
    private final Path filePath;
    private final String fileName;
    public Collection<NameSpace> nameSpaces = List.of();

    public ProcessContext(ProjectContext projectContext, String filePath) {
        this.projectContext = projectContext;
        this.filePath = Path.of(filePath);
        this.fileName = getFileName(this.filePath);
    }

    private static String getFileName(Path filePath) {
        return filePath.getFileName().toString();
    }

    @Override
    public String getNextAnonymousProcessName() {
        return projectContext.getNextAnonymousProcessName();
    }

    @Override
    public String getAnonymousXSLTName() {
        return "Transform" + nextAnonXSLTIndex++;
    }

    @Override
    public String getAnonUnhandledActivityName() {
        return projectContext.getAnonUnhandledActivityName();
    }

    public TypeContext typeContext() {
        return new TypeContext(projectContext);
    }

    public String fileName() {
        return this.fileName;
    }

    public String getFileContent(String relativePath) throws IOException {
        return projectContext.getFileContent(relativePath);
    }
}
