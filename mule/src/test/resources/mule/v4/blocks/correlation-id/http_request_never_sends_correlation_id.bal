import ballerina/http;

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

public listener http:Listener listener_config = new (8081);

service / on listener_config {
    function init() returns error? {
    }

    resource function default 'never(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};

        // http client request
        http:Client request_config = check new ("localhost:8082");
        http:Response clientResult0 = check request_config->/orders.get();
        ctx.payload = check clientResult0.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
