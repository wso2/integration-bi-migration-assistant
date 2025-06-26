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

service /mule\-3 on config {
    resource function get v\-1/demo/'1\.0/main\-contract/'new(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        log:printInfo("xxx: logger invoked");

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
