public class Constants {
    // Components
    public static final String LOGGER = "logger";

    // HTTP
    public static final String HTTP_LISTENER_CONFIG = "http:listener-config";
    public static final String HTTP_LISTENER = "http:listener";
    public static final String HTTP_LISTENER_CONNECTION = "http:listener-connection";

    public static final String HTTP_REQUEST = "http:request";
    public static final String HTTP_QUERY_PARAMS = "http:query-params";

    // Scopes
    public static final String FLOW = "flow";

    // Flow Control
    public static final String CHOICE = "choice";

    // Transformers
    public static final String SET_VARIABLE = "set-variable";
    public static final String SET_PAYLOAD = "set-payload";


    public static final String ORG_BALLERINA = "ballerina";
    public static final String MODULE_HTTP = "http";
    public static final String MODULE_LOG = "log";

    // Misc
    public static final String HTTP_RESOURCE_RETURN_TYPE_UPPER = "anydata|http:Response|http:StatusCodeResponse|" +
            "stream<http:SseEvent, error?>|stream<http:SseEvent, error>|error";
    public static final String HTTP_RESOURCE_RETURN_TYPE_DEFAULT = "http:Response|error";
    public static final String HTTP_ENDPOINT_METHOD_NAME = "invokeEndPoint";


    // Variables
    public static final String VAR_RESPONSE = "_response_";
    public static final String VAR_CLIENT = "_client_";
    public static final String VAR_CLIENT_GET = "_client_get_";
}
