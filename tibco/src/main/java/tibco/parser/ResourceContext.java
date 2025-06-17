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

import java.nio.file.Path;

public class ResourceContext implements Context {

    private final ProjectContext projectContext;
    private final Path filePath;

    public ResourceContext(ProjectContext projectContext, String filePath) {
        this.projectContext = projectContext;
        this.filePath = Path.of(filePath);
    }

    @Override
    public String getNextAnonymousProcessName() {
        return projectContext.getNextAnonymousProcessName();
    }

    @Override
    public String getAnonymousXSLTName() {
        return projectContext.getAnonymousXSLTName();
    }

    @Override
    public String getAnonUnhandledActivityName() {
        return projectContext.getAnonUnhandledActivityName();
    }

    public String getName() {
        return filePath.getFileName().toString();
    }
}
