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

service /mule4 on config {
    resource function get set_payload(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};

        // set payload
        string payload0 = "First payload";
        ctx.payload = payload0;

        // set payload
        string payload1 = "Second payload";
        ctx.payload = payload1;

        // set payload
        string payload2 = "Third payload";
        ctx.payload = payload2;

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
