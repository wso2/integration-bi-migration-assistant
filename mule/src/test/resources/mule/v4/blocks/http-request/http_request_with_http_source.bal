import ballerina/http;
import ballerina/log;

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

service /mule4 on listener_config {
    function init() returns error? {
    }

    resource function get http_request(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};

        // http client request
        http:Client http_request_config = check new ("https://jsonplaceholder.typicode.com:80");
        http:Response clientResult0 = check http_request_config->/posts/latest.get();
        ctx.payload = check clientResult0.getJsonPayload();
        log:printInfo(string `Received from external API: ${ctx.payload.toString()}`);

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
