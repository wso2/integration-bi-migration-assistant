import ballerina/http;
import ballerina/log;
import ballerina/uuid;

public type Vars record {|
    anydata trace?;
    string correlationId?;
    anydata fromVars?;
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

    resource function default main(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};
        traceSubFlow(ctx);

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

public function traceSubFlow(Context ctx) {
    ctx.vars.trace = {correlationId: ctx.correlationId};
    ctx.vars.correlationId = "manual";
    ctx.vars.fromVars = ctx.vars?.correlationId;
    log:printInfo(string `Processing ${ctx.correlationId.toString()}`);
}

function getCorrelationId(http:Request request) returns string {
    // Mule takes the caller's X-Correlation-ID, then its MULE_CORRELATION_ID, and makes an ID when neither is sent
    string|http:HeaderNotFoundError id = request.getHeader("X-Correlation-ID");
    if id !is string {
        id = request.getHeader("MULE_CORRELATION_ID");
    }
    return id is string ? id : uuid:createType4AsString();
}
