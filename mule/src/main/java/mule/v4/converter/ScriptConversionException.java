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
package mule.v4.converter;

/**
 * Exception thrown when MEL expression conversion fails.
 */
public class ScriptConversionException extends Exception {

    private final String melExpression;

    public ScriptConversionException(String melExpression, Throwable cause) {
        super("Failed to convert MEL expression: " + melExpression, cause);
        this.melExpression = melExpression;
    }

    public ScriptConversionException(String melExpression) {
        super("Failed to convert MEL expression: " + melExpression);
        this.melExpression = melExpression;
    }

    public String getMelExpression() {
        return melExpression;
    }
}

