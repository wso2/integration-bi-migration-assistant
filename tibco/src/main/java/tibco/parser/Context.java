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

import org.w3c.dom.Element;

public interface Context {

    String getNextAnonymousProcessName();

    String getAnonymousXSLTName();

    String getAnonUnhandledActivityName();

    void registerUnhandledActivity(Element element, String name, String type, String fileName);

    void registerPartiallySupportedActivity(Element element, String name, String type);

    void registerUnsupportedResource(Element element, String name);

    void registerPartiallySupportedResource(Element element, String name);

    void registerUnsupportedTransition(Element element);

    void registerUnsupportedSchema(Element element);

    void registerPartiallySupportedSchema(Element element);

    void registerUnsupportedWSDLDefinition(Element element);

    void registerPartiallySupportedWSDLDefinition(Element element);

    void incrementActivityCount(Element element);
}
