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

service /mule3 on config {
    Context ctx;

    function init() {
        self.ctx = {payload: (), inboundProperties: {response: new}};
    }

    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    private function _invokeEndPoint0_(Context ctx) returns http:Response|error {
        log:printInfo("xxx: INFO level logger invoked");
        log:printDebug("xxx: DEBUG level logger invoked");
        log:printError("xxx: ERROR level logger invoked");
        log:printWarn("xxx: WARN level logger invoked");
        log:printInfo("xxx: TRACE level logger invoked");
        return ctx.inboundProperties.response;
    }
}
