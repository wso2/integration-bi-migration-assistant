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
package mule.v4;

import common.BallerinaModel.Import;
import common.BallerinaModel.Parameter;
import common.BallerinaModel.TypeDesc.BallerinaType;

import java.util.Collections;
import java.util.List;

import static common.ConversionUtils.typeFrom;

public class Constants {

    // Ballerina Import
    public static final String ORG_BALLERINA = "ballerina";
    public static final String ORG_BALLERINAX = "ballerinax";
    public static final String MODULE_HTTP = "http";
    public static final String MODULE_JMS = "java.jms";
    public static final String MODULE_PUBSUB = "gcloud.pubsub";
    public static final String MODULE_FILE = "file";
    public static final String MODULE_REGEX = "regex";
    public static final String MODULE_LOG = "log";
    public static final String MODULE_TASK = "task";
    public static final String MODULE_SQL = "sql";
    public static final String MODULE_MYSQL = "mysql";
    public static final String MODULE_MYSQL_DRIVER = "mysql.driver";
    public static final String MODULE_ORACLEDB = "oracledb";
    public static final String MODULE_ORACLEDB_DRIVER = "oracledb.driver";
    public static final String MODULE_JAVA_JDBC = "java.jdbc";
    public static final String MODULE_TIME = "time";
    public static final String MODULE_INT = "lang.'int";
    public static final String MODULE_JAVA = "jballerina.java";
    public static final String MODULE_RUNTIME = "lang.runtime";

    // Context Type Def
    public static final String CONTEXT_RECORD_TYPE = "Context";
    public static final String VARS_TYPE = "Vars";
    public static final String ATTRIBUTES_TYPE = "Attributes";
    public static final String CONTEXT_REFERENCE = "ctx";
    public static final String VARS_REF = "vars";
    public static final String ATTRIBUTES_REF = "attributes";
    public static final String HTTP_REQUEST_REF = "request";
    public static final String HTTP_RESPONSE_REF = "response";
    public static final String PAYLOAD_REF = "payload";
    public static final String URI_PARAMS_REF = "uriParams";
    public static final String VARS_FIELD_ACCESS = CONTEXT_REFERENCE + "." + VARS_REF;
    public static final String ATTRIBUTES_FIELD_ACCESS = CONTEXT_REFERENCE + "." + ATTRIBUTES_REF;
    public static final String PAYLOAD_FIELD_ACCESS = CONTEXT_REFERENCE + "." + PAYLOAD_REF;

    // Variable/Func Names
    public static final String ON_FAIL_ERROR_VAR_REF = "err";
    public static final String VAR_ITERATOR = "_iterator_";
    public static final String FUNC_NAME_HTTP_ENDPOINT_TEMPLATE = "invokeEndPoint%s";
    public static final String VAR_DB_STREAM_TEMPLATE = "dbStream%s";
    public static final String VAR_DB_QUERY_TEMPLATE = "dbQuery%s";
    public static final String VAR_DB_SELECT_TEMPLATE = "dbSelect%s";
    public static final String FUNC_NAME_ENRICHER_TEMPLATE = "enricher%s";
    public static final String FUNC_NAME_ASYC_TEMPLATE = "async%s";
    public static final String VAR_PAYLOAD_TEMPLATE = "payload%s";
    public static final String VAR_CLIENT_RESULT_TEMPLATE = "clientResult%s";
    public static final String FUNC_NAME_VM_RECEIVE_TEMPLATE = "vmReceive%s";
    public static final String VAR_ITERATOR_TEMPLATE = "item%s";
    public static final String VAR_ORIGINAL_PAYLOAD_TEMPLATE = "originalPayload%s";
    public static final String VAR_WORKER_RESULT_TEMPLATE = "workerResults%s";
    public static final String VAR_SCATTER_GATHER_TEMPLATE = "scatterGatherResults%s";
    public static final String WORKER_SCATTER_GATHER = "R%s";
    public static final String FUNC_FIRST_SUCCESSFUL = "firstSuccessful%s";
    public static final String VAR_FIRST_SUCCESSFUL_RESULT = "firstSuccessfulResult%s";
    public static final String FUNC_FIRST_SUCCESSFUL_ROUTE = "route%s";
    public static final String FUNC_WRAP_ROUTE_ERR = "wrapRouteErrorIfExists";

    // Types
    public static final String HTTP_RESPONSE_TYPE = "http:Response";
    public static final String HTTP_REQUEST_TYPE = "http:Request";
    public static final String HTTP_CLIENT_TYPE = "http:Client";
    public static final String HTTP_RESOURCE_RETURN_TYPE_DEFAULT = HTTP_RESPONSE_TYPE + "|error";
    public static final String JMS_MESSAGE_TYPE = "jms:Message";
    public static final String PUBSUB_MESSAGE_TYPE = "pubsub:Message";
    public static final String PUBSUB_CALLER_TYPE = "pubsub:Caller";
    public static final String FILE_EVENT_TYPE = "file:FileEvent";
    public static final String MYSQL_CLIENT_TYPE = "mysql:Client";
    public static final String ORACLEDB_CLIENT_TYPE = "oracledb:Client";
    public static final String JDBC_CLIENT_TYPE = "jdbc:Client";
    public static final String HTTP_RESOURCE_RETURN_TYPE_UPPER = "anydata|http:Response|http:StatusCodeResponse|" +
            "stream<http:SseEvent, error?>|stream<http:SseEvent, error>|error";
    public static final String DB_QUERY_DEFAULT_TEMPLATE = "stream<%s, sql:Error?>";
    public static final String GENERIC_RECORD_TYPE = "record {}";
    public static final String GENERIC_RECORD_TYPE_REF = "Record";
    public static final String SQL_PARAMETERIZED_QUERY_TYPE = "sql:ParameterizedQuery";

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
