import ballerina/http;

public type Attributes record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

public listener http:Listener config = new (8081);

service /foo on config {
    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        json _dwOutput_ = _dwMethod0_(ctx.payload.toJson());
        json myVariable = _dwMethod0_(ctx.payload.toJson());
        ctx.payload = _dwOutput_;

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}

function _dwMethod0_(json payload) returns json {
    return "apple".toUpperAscii();
}
