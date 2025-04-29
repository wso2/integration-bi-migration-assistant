/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package mule;

import common.BallerinaModel.Import;
import common.BallerinaModel.Parameter;
import common.BallerinaModel.TypeDesc.BallerinaType;

import java.util.Collections;
import java.util.List;

import static mule.ConversionUtils.typeFrom;

public class Constants {

    // Ballerina Import
    public static final String ORG_BALLERINA = "ballerina";
    public static final String ORG_BALLERINAX = "ballerinax";
    public static final String MODULE_HTTP = "http";
    public static final String MODULE_LOG = "log";
    public static final String MODULE_SQL = "sql";
    public static final String MODULE_MYSQL = "mysql";
    public static final String MODULE_MYSQL_DRIVER = "mysql.driver";
    public static final String MODULE_TIME = "time";
    public static final String MODULE_INT = "lang.'int";
    public static final String MODULE_JAVA = "jballerina.java";

    // Variable/Func Names
    public static final String VAR_ITERATOR = "_iterator_";
    public static final String CONTEXT_REFERENCE = "ctx";
    public static final String FLOW_VARS_REF = "flowVars";
    public static final String SESSION_VARS_REF = "sessionVars";
    public static final String INBOUND_PROPERTIES_REF = "inboundProperties";
    public static final String FLOW_VARS_FIELD_ACCESS = CONTEXT_REFERENCE + "." + FLOW_VARS_REF;
    public static final String SESSION_VARS_FIELD_ACCESS = CONTEXT_REFERENCE + "." + SESSION_VARS_REF;
    public static final String INBOUND_PROPERTIES_FIELD_ACCESS = CONTEXT_REFERENCE + "." + INBOUND_PROPERTIES_REF;

    public static final String FUNC_NAME_HTTP_ENDPOINT_TEMPLATE = "_invokeEndPoint%s_";
    public static final String VAR_DB_STREAM_TEMPLATE = "_dbStream%s_";
    public static final String VAR_DB_QUERY_TEMPLATE = "_dbQuery%s_";
    public static final String VAR_DB_SELECT_TEMPLATE = "_dbSelect%s_";
    public static final String VAR_OBJ_TO_JSON_TEMPLATE = "_to_json%s_";
    public static final String VAR_OBJ_TO_STRING_TEMPLATE = "_to_string%s_";
    public static final String FUNC_NAME_ENRICHER_TEMPLATE = "_enricher%s_";
    public static final String FUNC_NAME_ASYC_TEMPLATE = "_async%s_";
    public static final String VAR_PAYLOAD_TEMPLATE = "_payload%s_";
    public static final String VAR_CLIENT_RESULT_TEMPLATE = "_clientResult%s_";
    public static final String FUNC_NAME_VM_RECEIVE_TEMPLATE = "_vmReceive%s_";

    // Types
    public static final String HTTP_RESPONSE_TYPE = "http:Response";
    public static final String HTTP_REQUEST_TYPE = "http:Request";
    public static final String HTTP_RESOURCE_RETURN_TYPE_DEFAULT = HTTP_RESPONSE_TYPE + "|error";
    public static final String MYSQL_CLIENT_TYPE = "mysql:Client";
    public static final String HTTP_RESOURCE_RETURN_TYPE_UPPER = "anydata|http:Response|http:StatusCodeResponse|" +
            "stream<http:SseEvent, error?>|stream<http:SseEvent, error>|error";
    public static final String DB_QUERY_DEFAULT_TEMPLATE = "stream<%s, sql:Error?>";
    public static final String GENERIC_RECORD_TYPE = "record {}";
    public static final String GENERIC_RECORD_TYPE_REF = "Record";
    public static final String SQL_PARAMETERIZED_QUERY_TYPE = "sql:ParameterizedQuery";
    public static final String CONTEXT_RECORD_TYPE = "Context";
    public static final String FLOW_VARS_TYPE = "FlowVars";
    public static final String SESSION_VARS_TYPE = "SessionVars";
    public static final String INBOUND_PROPERTIES_TYPE = "InboundProperties";

    //DataWeave
    public static final String CLASSPATH_DIR = "src/main/resources/";
    public static final String CLASSPATH = "classpath:";

    // Misc
    public static final Import HTTP_MODULE_IMPORT = new Import(ORG_BALLERINA, MODULE_HTTP);
    public static final Parameter CONTEXT_FUNC_PARAM = new Parameter(Constants.CONTEXT_REFERENCE,
            typeFrom(Constants.CONTEXT_RECORD_TYPE));
    public static final List<Parameter> FUNC_PARAMS_WITH_CONTEXT = Collections.singletonList(CONTEXT_FUNC_PARAM);

    public static final BallerinaType BAL_INT_TYPE = typeFrom("int");
    public static final BallerinaType BAL_STRING_TYPE = typeFrom("string");
    public static final BallerinaType BAL_ERROR_TYPE = typeFrom("error");
    public static final BallerinaType BAL_HANDLE_TYPE = typeFrom("handle");
    public static final BallerinaType BAL_ANYDATA_TYPE = typeFrom("anydata");
}
