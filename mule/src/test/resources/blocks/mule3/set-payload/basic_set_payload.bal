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

service /mule3 on config {
    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};

        // set payload
        string payload0 = "Hello world!";
        ctx.payload = payload0;

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
