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
        json _dwOutput_ = check _dwMethod0_(ctx);
        ctx.payload = _dwOutput_;

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}

function _dwMethod0_(Context ctx) returns json|error {
    return {
        "s1": "Hello World",
        "s2": "Hello World",
        "n": 1.23,
        "b": true,
        "a": check [1, 2, 3].ensureType(json),
        "o": check {"name": "Anne"}.ensureType(json)
    };
}
