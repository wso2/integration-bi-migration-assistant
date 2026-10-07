/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package mule.v4;

import mule.common.apispec.ApiSpec;
import mule.common.apispec.ApiSpec.Operation;
import mule.common.apispec.ApiTypeGenerator;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;

/**
 * A success response of a spec operation whose JSON body a resource that checks its responses checks.
 *
 * @param status        HTTP status code, from 200 to 299
 * @param type          Ballerina type the resource returns for the status: a response record when the body has a
 *                      named spec type, otherwise the {@code http} record of the status, whose body is any JSON
 * @param hasRecordBody whether the body is checked against a spec record rather than only as JSON
 */
public record SuccessResponse(int status, String type, boolean hasRecordBody) {

    private static final Map<Integer, String> STATUS_TYPES = Map.of(200, "http:Ok", 201, "http:Created",
            202, "http:Accepted", 203, "http:NonAuthoritativeInformation", 204, "http:NoContent",
            205, "http:ResetContent", 206, "http:PartialContent", 207, "http:MultiStatus",
            208, "http:AlreadyReported", 226, "http:IMUsed");

    public SuccessResponse {
        assert STATUS_TYPES.containsKey(status) && type != null;
    }

    /**
     * Returns the success responses of an operation that declare a JSON body, in status order. A body of a named spec
     * type is checked against its record, which the type generator then writes with a response record; any other
     * body is checked only as JSON. A success response without a body says nothing to check, and one that may also be
     * another media type cannot be checked, so neither is returned.
     *
     * @param operation     the spec operation
     * @param typeGenerator generator of the spec's types
     * @return the success responses to check
     */
    public static @NotNull List<SuccessResponse> of(Operation operation, ApiTypeGenerator typeGenerator) {
        assert operation != null && typeGenerator != null;
        return operation.responses().entrySet().stream()
                .filter(entry -> STATUS_TYPES.containsKey(entry.getKey()))
                .sorted(Map.Entry.comparingByKey())
                .flatMap(entry -> jsonBody(entry.getValue()).stream()
                        .map(body -> successResponse(entry.getKey(), body, typeGenerator)))
                .toList();
    }

    private static SuccessResponse successResponse(int status, ApiSpec.Body body, ApiTypeGenerator typeGenerator) {
        String statusType = STATUS_TYPES.get(status);
        Optional<ApiSpec.ApiType> recordType = body.type().filter(typeGenerator::isNamedType);
        return new SuccessResponse(status, recordType.map(type -> typeGenerator.responseType(statusType, type))
                .orElse(statusType), recordType.isPresent());
    }

    private static Optional<ApiSpec.Body> jsonBody(ApiSpec.Response response) {
        Map<String, ApiSpec.Body> bodies = response.bodies();
        if (bodies.size() != 1 || !SpecResourceSignature.isJsonMediaType(bodies.keySet().iterator().next())) {
            return Optional.empty();
        }
        return Optional.of(bodies.values().iterator().next());
    }
}
