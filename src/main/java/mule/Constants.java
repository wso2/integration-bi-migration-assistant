package mule;

public class Constants {

    // Global Elements
    public static final String HTTP_LISTENER_CONFIG = "http:listener-config";
    public static final String DB_MYSQL_CONFIG = "db:mysql-config";

    // Components
    public static final String LOGGER = "logger";

    // Flow Control
    public static final String CHOICE = "choice";

    // Scopes
    public static final String FLOW = "flow";
    public static final String SUB_FLOW = "sub-flow";
    public static final String FLOW_REFERENCE = "flow-ref";

    // Transformers
    public static final String SET_VARIABLE = "set-variable";
    public static final String SET_PAYLOAD = "set-payload";

    // HTTP Module
    public static final String HTTP_LISTENER = "http:listener";
    public static final String HTTP_LISTENER_CONNECTION = "http:listener-connection";
    public static final String HTTP_REQUEST = "http:request";
    public static final String HTTP_QUERY_PARAMS = "http:query-params";

    // Database Connector
    public static final String DB_SELECT = "db:select";

    // Ballerina Import
    public static final String ORG_BALLERINA = "ballerina";
    public static final String ORG_BALLERINAX = "ballerinax";
    public static final String MODULE_HTTP = "http";
    public static final String MODULE_LOG = "log";
    public static final String MODULE_SQL = "sql";
    public static final String MODULE_MYSQL = "mysql";
    public static final String MODULE_MYSQL_DRIVER = "mysql.driver";

    // Variables
    public static final String VAR_RESPONSE = "_response_";
    public static final String VAR_CLIENT = "_client_";
    public static final String VAR_CLIENT_GET = "_client_get_";

    // Types
    public static final String HTTP_RESOURCE_RETURN_TYPE_DEFAULT = "http:Response|error";
    public static final String MYSQL_CLIENT = "mysql:Client";
    public static final String HTTP_RESOURCE_RETURN_TYPE_UPPER = "anydata|http:Response|http:StatusCodeResponse|" +
            "stream<http:SseEvent, error?>|stream<http:SseEvent, error>|error";
    public static final String DB_QUERY_DEFAULT_TEMPLATE = "stream<%s, sql:Error?>";

    // Misc
    public static final String METHOD_NAME_HTTP_ENDPOINT_TEMPLATE = "_invokeEndPoint%s_";
    public static final String VAR_DB_STREAM_TEMPLATE = "_dbStream%s_";
}
