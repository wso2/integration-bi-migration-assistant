import ballerina/http;
import ballerina/log;

public type InboundProperties record {|
    http:Response response;
    http:Request request;
    map<string> uriParams;
|};

public type Context record {|
    anydata payload;
    InboundProperties inboundProperties;
|};

public listener http:Listener config = new (8081);

service /mule3 on config {
    Context ctx;

    function init() {
        self.ctx = {payload: (), inboundProperties: {response: new, request: new, uriParams: {}}};
    }

    resource function get .(http:Request request) returns http:Response|error {
        self.ctx.inboundProperties.request = request;
        self.ctx.inboundProperties.response = new;
        return invokeEndPoint0(self.ctx);
    }
}

public function invokeEndPoint0(Context ctx) returns http:Response|error {
    log:printInfo("xxx: logger invoked via http end point");
    demoSub_Flow(ctx);
    log:printInfo("xxx: logger after flow reference invoked");

    ctx.inboundProperties.response.setPayload(ctx.payload);
    return ctx.inboundProperties.response;
}

public function demoSub_Flow(Context ctx) {
    log:printInfo("xxx: sub flow logger invoked");

    // set payload
    string payload0 = "This is a sub flow set-payload call";
    ctx.payload = payload0;
}
