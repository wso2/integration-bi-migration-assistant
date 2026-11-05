import ballerina/http;
import ballerina/log;
import ballerina/uuid;

public type Vars record {|
    anydata cid1?;
    any cid2?;
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

service / on listener_config {
    function init() returns error? {
    }

    resource function default demo(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};
        ctx.vars.cid1 = (<string?>ctx.correlationId) ?: "";
        any cid2 = _dwMethod(ctx);
        ctx.vars.cid2 = cid2;
        log:printInfo("cid: " + ctx.correlationId.toString());
        log:printInfo((<string?>ctx.correlationId) ?: "none");

        // http client request
        http:Client request_config = check new ("localhost:8082");
        http:Response clientResult0 = check request_config->/orders.get(headers = {"X-Correlation-ID": ctx.vars?.cid1.toString()});
        ctx.payload = check clientResult0.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

public function _dwMethod(Context ctx) returns any => ctx.correlationId;

function getCorrelationId(http:Request request) returns string {
    // Mule takes the caller's X-Correlation-ID, then its MULE_CORRELATION_ID, and makes an ID when neither is sent
    string|http:HeaderNotFoundError id = request.getHeader("X-Correlation-ID");
    if id !is string {
        id = request.getHeader("MULE_CORRELATION_ID");
    }
    return id is string ? id : uuid:createType4AsString();
}
