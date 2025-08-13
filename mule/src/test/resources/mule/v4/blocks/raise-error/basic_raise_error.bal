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
    resource function default test(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        if ctx.attributes.request.getQueryParamValue("age") < 16 {
            fail error PRECONDITIONS__INCORRECT_AGE("Minimum age of 16 required to drive");
        } else {
            log:printInfo("User age above 16 years. Allowed to drive");
        }

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}
