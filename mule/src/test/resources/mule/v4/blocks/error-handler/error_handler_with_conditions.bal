import ballerina/http;
import ballerina/log;

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

public listener http:Listener listener_config = new (8083);

service /mule4 on listener_config {
    function init() returns error? {
    }

    resource function get error_handler(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        do {

            // set payload
            anydata payload0 = 1 / 0;
            ctx.payload = payload0;
            log:printInfo("xxx: log after exception");
        } on fail error err {
            my_error_handler(ctx, err);
        }

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

public function my_error_handler(Context ctx, error err) {
    // TODO: if conditions may require some manual adjustments
    if err is "ANY" && err.message() == "#[error.description contains 'timeout']" {

        // on-error-propagate

        log:printError("Message: " + err.message());
        log:printError("Trace: " + err.stackTrace().toString());

        log:printInfo("xxx: first error catch");
        http:Response response = <http:Response>ctx.attributes.response;
        response.statusCode = 500;
    } else if err is "EXPRESSION" {
        // on-error-continue
        log:printError("Message: " + err.message());
        log:printError("Trace: " + err.stackTrace().toString());

        log:printInfo("xxx: second error catch");
    } else if err.message() == "#[error.cause.'type' == 'java.lang.NullPointerException']" {
        // on-error-continue
        log:printError("Message: " + err.message());
        log:printError("Trace: " + err.stackTrace().toString());

        log:printInfo("xxx: last error catch");
    }
}
