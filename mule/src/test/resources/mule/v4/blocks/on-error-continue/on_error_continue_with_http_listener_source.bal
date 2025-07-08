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

public listener http:Listener listener_config = new (8081);

service /mule4 on listener_config {
    resource function get on_error_continue(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        do {

            // set payload
            anydata payload0 = 1 / 0;
            ctx.payload = payload0;
            log:printInfo("xxx: log after exception");
        } on fail error e {
            // on-error-continue
            log:printError("Message: " + e.message());
            log:printError("Trace: " + e.stackTrace().toString());

            log:printInfo("xxx: error handled in on-error-continue");

            // set payload
            string payload1 = "Default value: Error occurred but we handled it.";
            ctx.payload = payload1;
        }

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
