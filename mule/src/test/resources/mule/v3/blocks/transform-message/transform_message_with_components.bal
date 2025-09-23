import ballerina/http;

public type InboundProperties record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    InboundProperties inboundProperties;
|};

public listener http:Listener config = new (8081);

service /foo on config {
    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        json myVariable = _dwMethod0_(ctx.payload.toJson());
        json _dwOutput_ = _dwMethod0_(ctx.payload.toJson());
        json mySessionVariable = _dwMethod0_(ctx.payload.toJson());
        ctx.payload = _dwOutput_;

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}

function _dwMethod0_(Context ctx) returns json {
    return "apple".toUpperAscii();
}
