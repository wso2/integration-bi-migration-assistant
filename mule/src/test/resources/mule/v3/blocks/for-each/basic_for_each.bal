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

public listener http:Listener HTTP_Listener_config = new (9090);

service / on HTTP_Listener_config {
    resource function post demo(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};

        // set payload
        string payload0 = "[\"Alice\", \"Bob\", \"Charlie\"]";
        ctx.payload = payload0;

        // foreach loop
        anydata originalPayload0 = ctx.payload;
        foreach anydata iterator0 in ctx.payload {
            ctx.payload = iterator0;
            log:printInfo(string `Current item: ${ctx.payload.toString()}`);
        }
        ctx.payload = originalPayload0;

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
