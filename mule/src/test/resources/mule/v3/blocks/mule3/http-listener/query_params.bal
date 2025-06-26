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

service /demo on config {
    resource function get testquery(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        log:printInfo("xxx: logger invoked");
        log:printInfo(string `Path params - version: ${ctx.inboundProperties.request.getQueryParamValue("country").toString()}, id: ${ctx.inboundProperties.request.getQueryParamValue("city").toString()}`);

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
