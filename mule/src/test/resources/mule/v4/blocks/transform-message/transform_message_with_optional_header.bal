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

public listener http:Listener listener_config = new (8081);

service /mule4 on listener_config {
    function init() returns error? {
    }

    resource function get orders(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        json _dwOutput_ = check transformMessage(ctx);
        ctx.vars._dwOutput_ = _dwOutput_;
        ctx.payload = _dwOutput_;

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

public function transformMessage(Context ctx) returns json|error => {
    "token": check ((<http:Request>ctx.attributes.request).hasHeader("token") ? (<http:Request>ctx.attributes.request).getHeader("token") : ()),
    "tokenOrDefault": (check ((<http:Request>ctx.attributes.request).hasHeader("token") ? (<http:Request>ctx.attributes.request).getHeader("token") : ())) ?: "none",
    "noToken": check ((<http:Request>ctx.attributes.request).hasHeader("token") ? (<http:Request>ctx.attributes.request).getHeader("token") : ()) == ()
};
