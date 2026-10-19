import ballerina/http;
import ballerina/uuid;

public type Vars record {|
    json _dwOutput_?;
    json ids?;
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

    resource function default dw(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};
        json _dwOutput_ = _dwMethod(ctx);
        ctx.vars._dwOutput_ = _dwOutput_;
        ctx.payload = _dwOutput_;
        json ids = check _dwMethod1(ctx);
        ctx.vars.ids = ids;

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

public function _dwMethod(Context ctx) returns json {
    string correlationId = "local";
    return {
        "correlationId": correlationId,
        "source": "script"
    };
}

public function _dwMethod1(Context ctx) returns json|error => {
    "x-correlation-id": check ctx.correlationId.ensureType(json),
    "id": (<string?>ctx.correlationId) ?: "none"
};

function getCorrelationId(http:Request request) returns string {
    // Mule takes the caller's X-Correlation-ID, then its MULE_CORRELATION_ID, and makes an ID when neither is sent
    string|http:HeaderNotFoundError id = request.getHeader("X-Correlation-ID");
    if id !is string {
        id = request.getHeader("MULE_CORRELATION_ID");
    }
    return id is string ? id : uuid:createType4AsString();
}
