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
    // on-error-propagate
    log:printError("Message: " + err.message());
    log:printError("Trace: " + err.stackTrace().toString());

    log:printInfo("Error handled in on-error-propagate");

    // set payload
    string payload1 = "Custom error message: Something went wrong.";
    ctx.payload = payload1;
    http:Response response = <http:Response>ctx.attributes.response;
    response.statusCode = 500;
}
