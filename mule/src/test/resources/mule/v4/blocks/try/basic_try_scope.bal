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

public type PRECONDITIONS__INCORRECT_AGE distinct error;

public listener http:Listener HTTP_Listener_config = new (8081);

service / on HTTP_Listener_config {
    resource function post checkAge(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        log:printInfo(string `Start of flow - Payload received: ${ctx.payload.toString()}`);
        do {
            if ctx.payload.age is Number < 16 {
                fail error PRECONDITIONS__INCORRECT_AGE("Age is below minimum requirement");
            } else {
                log:printInfo(string `Age is acceptable: ${ctx.payload.age.toString()}`);
            }
        } on fail error err {
            // on-error-continue
            log:printError("Message: " + err.message());
            log:printError("Trace: " + err.stackTrace().toString());

            // set payload
            anydata payload0 = string `{ "status": "error", "message": "${err.message()}" }`;
            ctx.payload = payload0;
        }
        log:printInfo("End of flow");

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}
