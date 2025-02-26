import ballerina/http;
import ballerina/log;

public type InboundProperties record {|
    http:Response response;
|};

public type Context record {|
    anydata payload;
    InboundProperties inboundProperties;
|};

public listener http:Listener config = new (8081, {host: "0.0.0.0"});

service / on config {
    Context ctx;

    function init() {
        self.ctx = {payload: (), inboundProperties: {response: new}};
    }

    resource function get demo() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    private function _invokeEndPoint0_(Context ctx) returns http:Response|error {
        log:printInfo("xxx: logger invoked");
        return ctx.inboundProperties.response;
    }
}
