import ballerina/http;
import ballerina/uuid;

public type Vars record {|
    string requester_fields?;
|};

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
    Attributes attributes;
    string correlationId = uuid:createType4AsString();
|};

public listener http:Listener listener_config = new (8081);

service /mule4 on listener_config {
    function init() returns error? {
    }

    resource function get orders/[string orderId](http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {orderId}}, correlationId: getCorrelationId(request)};
        ctx.vars.requester_fields = "code,status";

        // http client request
        http:Client order\-hybris\-sapi\-http\-request\-configuration = check new ("https://orders.example.com:80");
        map<string?> headers_nilable = {
            "fields": ctx.vars?.requester_fields.toString()
        };
        map<string> headers = map from string key in headers_nilable.keys()
            where headers_nilable.get(key) is string
            select [key, <string>headers_nilable.get(key)];
        map<string?> uriParams_nilable = {
            "code": ctx.attributes.uriParams.get("orderId").toString(),
            "baseSiteId": getOptionalHeader(<http:Request>ctx.attributes.request, "marketid") ?: "gb"
        };
        map<string> uriParams = map from string key in uriParams_nilable.keys()
            where uriParams_nilable.get(key) is string
            select [key, <string>uriParams_nilable.get(key)];
        if headers.keys().every(key => !key.equalsIgnoreCaseAscii("X-Correlation-ID")) {
            headers["X-Correlation-ID"] = ctx.correlationId;
        }
        http:Response clientResult0 = check order\-hybris\-sapi\-http\-request\-configuration->/[uriParams.get("baseSiteId")]/orders/[uriParams.get("code")].get(headers);
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
