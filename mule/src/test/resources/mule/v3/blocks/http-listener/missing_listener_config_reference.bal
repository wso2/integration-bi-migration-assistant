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

// TODO: listener config ref 'config' not found. Using default base path '/'
service / on config {
    resource function get demo(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        log:printInfo("xxx: logger invoked");

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
