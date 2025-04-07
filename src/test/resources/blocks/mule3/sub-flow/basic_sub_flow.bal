import ballerina/http;
import ballerina/log;

type InboundProperties record {|
    http:Response response;
|};

type Context record {|
    anydata payload;
    InboundProperties inboundProperties;
|};

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /mule3 on config {
    Context ctx;

    function init() {
        self.ctx = {payload: (), inboundProperties: {response: new}};
    }

    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    private function _invokeEndPoint0_(Context ctx) returns http:Response|error {
        log:printInfo("xxx: logger invoked via http end point");
        demoSub_Flow(ctx);
        log:printInfo("xxx: logger after flow reference invoked");
        return ctx.inboundProperties.response;
    }
}

function demoSub_Flow(Context ctx) {
    log:printInfo("xxx: sub flow logger invoked");

    // set payload
    string _payload0_ = "This is a sub flow set-payload call";
    ctx.payload = _payload0_;
    ctx.inboundProperties.response.setPayload(_payload0_);
}
