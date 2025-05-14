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

service /mule3 on config {
    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        log:printInfo("xxx: logger invoked via http end point");
        demoPrivateFlow(ctx);
        log:printInfo("xxx: end of main flow");

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}

public function demoPrivateFlow(Context ctx) {
    log:printInfo("xxx: private flow invoked");
}
