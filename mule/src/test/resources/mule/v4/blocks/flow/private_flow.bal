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
    resource function get flow(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        log:printInfo("xxx: logger invoked via http end point");
        demoPrivateFlow(ctx);
        log:printInfo("xxx: end of main flow");

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}

public function demoPrivateFlow(Context ctx) {
    log:printInfo("xxx: private flow invoked");
}
