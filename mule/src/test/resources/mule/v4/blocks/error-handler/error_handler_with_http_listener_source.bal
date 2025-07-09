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

public listener http:Listener listener_config = new (8083);

service /mule4 on listener_config {
    resource function get error_handler(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        do {

            // set payload
            anydata payload0 = 1 / 0;
            ctx.payload = payload0;
            log:printInfo("xxx: log after exception");
        } on fail error e {
            my_error_handler(ctx, e);
        }

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}

public function my_error_handler(Context ctx, error e) {
    // on-error-propagate
    log:printError("Message: " + e.message());
    log:printError("Trace: " + e.stackTrace().toString());

    log:printInfo("Error handled in on-error-propagate");

    // set payload
    string payload1 = "Custom error message: Something went wrong.";
    ctx.payload = payload1;
    ctx.inboundProperties.response.statusCode = 500;
}
