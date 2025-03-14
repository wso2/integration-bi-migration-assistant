package mule;

public class Constants {

    // Global Elements
    public static final String HTTP_LISTENER_CONFIG = "http:listener-config";
    public static final String DB_MYSQL_CONFIG = "db:mysql-config";
    public static final String DB_TEMPLATE_QUERY = "db:template-query";

    // Components
    public static final String LOGGER = "logger";
    public static final String TRANSFORM_MESSAGE = "dw:transform-message";

    // Flow Control
    public static final String CHOICE = "choice";

    // Scopes
    public static final String FLOW = "flow";
    public static final String SUB_FLOW = "sub-flow";
    public static final String FLOW_REFERENCE = "flow-ref";
    public static final String ENRICHER = "enricher";

    // Transformers
    public static final String SET_VARIABLE = "set-variable";
    public static final String SET_SESSION_VARIABLE = "set-session-variable";
    public static final String SET_PAYLOAD = "set-payload";
    public static final String OBJECT_TO_JSON = "json:object-to-json-transformer";
    public static final String OBJECT_TO_STRING = "object-to-string-transformer";

    // Error Handling
    public static final String CATCH_EXCEPTION_STRATEGY = "catch-exception-strategy";

    // HTTP Module
    public static final String HTTP_LISTENER = "http:listener";
    public static final String HTTP_LISTENER_CONNECTION = "http:listener-connection";
    public static final String HTTP_REQUEST = "http:request";
    public static final String HTTP_QUERY_PARAMS = "http:query-params";

    // Database Connector
    public static final String DB_INSERT = "db:insert";
    public static final String DB_SELECT = "db:select";
    public static final String DB_UPDATE = "db:update";
    public static final String DB_DELETE = "db:delete";
    public static final String DB_PARAMETERIZED_QUERY = "db:parameterized-query";
    public static final String DB_DYNAMIC_QUERY = "db:dynamic-query";
    public static final String DB_TEMPLATE_QUERY_REF = "db:template-query-ref";

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

    // Variable/Method Names
    public static final String VAR_RESPONSE = "_response_";
    public static final String VAR_CLIENT = "_client_";
    public static final String VAR_CLIENT_GET = "_client_get_";
    public static final String VAR_ITERATOR = "_iterator_";

    public static final String METHOD_NAME_HTTP_ENDPOINT_TEMPLATE = "_invokeEndPoint%s_";
    public static final String VAR_DB_STREAM_TEMPLATE = "_dbStream%s_";
    public static final String VAR_DB_QUERY_TEMPLATE = "_dbQuery%s_";
    public static final String VAR_DB_SELECT_TEMPLATE = "_dbSelect%s_";
    public static final String VAR_OBJ_TO_JSON_TEMPLATE = "_to_json%s_";
    public static final String VAR_OBJ_TO_STRING_TEMPLATE = "_to_string%s_";
    public static final String METHOD_NAME_ENRICHER_TEMPLATE = "_enricher%s_";
    public static final String VAR_PAYLOAD_TEMPLATE = "_payload%s_";

    // Types
    public static final String HTTP_RESOURCE_RETURN_TYPE_DEFAULT = "http:Response|error";
    public static final String MYSQL_CLIENT_TYPE = "mysql:Client";
    public static final String HTTP_RESOURCE_RETURN_TYPE_UPPER = "anydata|http:Response|http:StatusCodeResponse|" +
            "stream<http:SseEvent, error?>|stream<http:SseEvent, error>|error";
    public static final String DB_QUERY_DEFAULT_TEMPLATE = "stream<%s, sql:Error?>";
    public static final String RECORD_TYPE = "record {}";
    public static final String SQL_PARAMETERIZED_QUERY_TYPE = "sql:ParameterizedQuery";

    //DataWeave
    public static final String INPUT_PAYLOAD = "input-payload";
    public static final String CLASSPATH_DIR = "src/main/resources/";
    public static final String CLASSPATH = "classpath:";
}
