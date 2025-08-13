import ballerina/http;
import ballerina/log;

public type Attributes record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

public listener http:Listener listener_config = new (8081);

service /mule4 on listener_config {
    resource function get on_error_continue(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        do {

            // set payload
            anydata payload0 = 1 / 0;
            ctx.payload = payload0;
            log:printInfo("xxx: log after exception");
        } on fail error err {
            // on-error-continue
            log:printError("Message: " + err.message());
            log:printError("Trace: " + err.stackTrace().toString());

            log:printInfo("xxx: error handled in on-error-continue");

            // set payload
            string payload1 = "Default value: Error occurred but we handled it.";
            ctx.payload = payload1;
        }

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}
