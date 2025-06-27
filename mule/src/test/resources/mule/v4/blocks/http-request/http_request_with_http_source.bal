import ballerina/http;
import ballerina/log;

public type InboundProperties record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    InboundProperties inboundProperties;
|};

public listener http:Listener HTTP_Listener_Config = new (8081);

service /mule3 on HTTP_Listener_Config {
    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};

        // http client request
        http:Client HTTP_Request_Config = check new ("jsonplaceholder.typicode.com:80");
        http:Response clientResult0 = check HTTP_Request_Config->/posts/latest.get();
        ctx.payload = check clientResult0.getJsonPayload();
        log:printInfo(string `Received from external API: ${ctx.payload.toString()}`);

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
