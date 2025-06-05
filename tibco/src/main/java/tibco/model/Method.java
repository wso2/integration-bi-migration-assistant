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

package tibco.model;

public enum Method {
    POST("post"),
    DELETE("delete"),
    PUT("put"),
    GET("get");

    public final String method;

    Method(String method) {
        this.method = method;
    }

    public static Method from(String value) {
        return switch (value.toLowerCase()) {
            case "post" -> POST;
            case "delete" -> DELETE;
            case "put" -> PUT;
            case "get" -> GET;
            default -> throw new IllegalArgumentException("Unknown method: " + value);
        };
    }
}
