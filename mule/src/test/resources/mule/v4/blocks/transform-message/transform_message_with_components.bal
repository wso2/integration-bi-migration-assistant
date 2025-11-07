import ballerina/http;

public type Vars record {|
    json _dwOutput_?;
    json myVariable?;
|};

public type Attributes record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
    Attributes attributes;
|};

public listener http:Listener config = new (8081);

service /foo on config {
    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        json _dwOutput_ = _dwMethod0_(ctx);
        ctx.vars._dwOutput_ = _dwOutput_;
        ctx.payload = _dwOutput_;
        json myVariable = _dwMethod0_(ctx);
        ctx.vars.myVariable = myVariable;

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}

function _dwMethod0_(Context ctx) returns json {
    return "apple".toUpperAscii();
}
