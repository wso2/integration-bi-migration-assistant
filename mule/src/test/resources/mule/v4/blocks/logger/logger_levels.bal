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

public listener http:Listener config = new (8081);

service /mule4 on config {
    resource function get logger(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        log:printInfo("xxx: INFO level logger invoked");
        log:printDebug("xxx: DEBUG level logger invoked");
        log:printError("xxx: ERROR level logger invoked");
        log:printWarn("xxx: WARN level logger invoked");
        log:printInfo("xxx: TRACE level logger invoked");

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
