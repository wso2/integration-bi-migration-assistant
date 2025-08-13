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

public listener http:Listener listener_config = new (8083);

service /mule4 on listener_config {
    resource function get on_error_propagate(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        do {

            // set payload
            anydata payload0 = 1 / 0;
            ctx.payload = payload0;
            log:printInfo("xxx: log after exception");
        } on fail error err {
            // on-error-propagate
            log:printError("Message: " + err.message());
            log:printError("Trace: " + err.stackTrace().toString());

            log:printInfo("Error handled in on-error-propagate");

            // set payload
            string payload1 = "Custom error message: Something went wrong.";
            ctx.payload = payload1;
            ctx.attributes.response.statusCode = 500;
        }

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}
