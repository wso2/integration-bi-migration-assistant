import ballerina/http;
import ballerina/uuid;

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
    string correlationId = uuid:createType4AsString();
|};

configurable string backend_enrich = ?;
configurable string backend_region = ?;
configurable string backend_limit = ?;
public listener http:Listener listener_config = new (8081);

service /mule4 on listener_config {
    function init() returns error? {
    }

    resource function get orders(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};

        // http client request
        http:Client backend = check new ("localhost:8082");
        map<string?> headers_nilable = {
            "enrichProducts": backend_enrich,
            "traceId": getOptionalHeader(<http:Request>ctx.attributes.request, "x-trace-id"),
            "region": (backend_region)
        };
        map<string> headers = map from string key in headers_nilable.keys()
            where headers_nilable.get(key) is string
            select [key, <string>headers_nilable.get(key)];
        map<string?> queryParams_nilable = {
            "limit": backend_limit,
            "page": ((<http:Request>ctx.attributes.request).getQueryParamValue("page"))
        };
        map<string> queryParams = map from string key in queryParams_nilable.keys()
            where queryParams_nilable.get(key) is string
            select [key, <string>queryParams_nilable.get(key)];
        if headers.keys().every(key => !key.equalsIgnoreCaseAscii("X-Correlation-ID")) {
            headers["X-Correlation-ID"] = ctx.correlationId;
        }
        http:Response clientResult0 = check backend->/orders.get(headers, params = check queryParams.cloneWithType());
        ctx.payload = check clientResult0.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

function getCorrelationId(http:Request request) returns string {
    // Mule takes the caller's X-Correlation-ID, then its MULE_CORRELATION_ID, and makes an ID when neither is sent
    string|http:HeaderNotFoundError id = request.getHeader("X-Correlation-ID");
    if id !is string {
        id = request.getHeader("MULE_CORRELATION_ID");
    }
    return id is string ? id : uuid:createType4AsString();
}

function getOptionalHeader(http:Request req, string name) returns string? {
    string|http:HeaderNotFoundError value = req.getHeader(name);
    return value is string ? value : ();
}
