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

public listener http:Listener config = new (8081);

service /mule4 on config {
    resource function get test_query(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        log:printInfo("xxx: logger invoked");
        log:printInfo(string `Path params - version: ${ctx.attributes.request.getQueryParamValue("country").toString()}, id: ${ctx.attributes.request.getQueryParamValue("city").toString()}`);

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}
