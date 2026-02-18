import ballerina/http;

public type Vars record {|
    json _dwOutput_?;
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
|};

public listener http:Listener config = new (8081);

service /foo on config {
    function init() returns error? {
    }

    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        json _dwOutput_ = transformMessage(ctx);
        ctx.vars._dwOutput_ = _dwOutput_;
        ctx.payload = _dwOutput_;

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

public function transformMessage(Context ctx) returns json => {
    "s1": "Hello World",
    "s2": "Hello World",
    "n": 1.23,
    "b": true,
    "a": [1, 2, 3],
    "o": {"name": "Anne"}
}.toJsonString();
