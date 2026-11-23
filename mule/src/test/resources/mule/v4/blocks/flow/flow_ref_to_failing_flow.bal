import ballerina/http;
import ballerina/log;
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

public listener http:Listener listener_config = new (8081);

service / on listener_config {
    function init() returns error? {
    }

    resource function default posts(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};
        check fetchPostFlow(ctx);
        check safeFetchFlow(ctx);
        check logSubFlow(ctx);

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

public function fetchPostFlow(Context ctx) returns error? {

    // http client request
    http:Client http_request_config = check new ("jsonplaceholder.typicode.com:80");
    http:Response clientResult0 = check http_request_config->/posts/latest.get(headers = {"X-Correlation-ID": ctx.correlationId});
    ctx.payload = check clientResult0.getJsonPayload();
    log:printInfo(string `Received from external API: ${ctx.payload.toString()}`);
}

public function safeFetchFlow(Context ctx) returns error? {
    do {

        // http client request
        http:Client http_request_config = check new ("jsonplaceholder.typicode.com:80");
        http:Response clientResult1 = check http_request_config->/posts/latest.get(headers = {"X-Correlation-ID": ctx.correlationId});
        ctx.payload = check clientResult1.getJsonPayload();
    } on fail error err {
        // on-error-continue
        log:printError("Fetch failed");
    }
}

public function logSubFlow(Context ctx) returns error? {
    log:printInfo("Done");
}

function getCorrelationId(http:Request request) returns string {
    // Mule takes the caller's X-Correlation-ID, then its MULE_CORRELATION_ID, and makes an ID when neither is sent
    string|http:HeaderNotFoundError id = request.getHeader("X-Correlation-ID");
    if id !is string {
        id = request.getHeader("MULE_CORRELATION_ID");
    }
    return id is string ? id : uuid:createType4AsString();
}
