import ballerina/http;

public type Vars record {|
    anydata orderType?;
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

service /mule4 on config {
    function init() returns error? {
    }

    resource function get sub_flow(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        ctx.vars.orderType = getOptionalHeader(<http:Request>ctx.attributes.request, "orderType");
        demoSubFlow(ctx);

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

public function demoSubFlow(Context ctx) {
    // TODO: failed to convert #[attributes.headers.'marketId']
}

function getOptionalHeader(http:Request req, string name) returns string? {
    string|http:HeaderNotFoundError value = req.getHeader(name);
    return value is string ? value : ();
}
