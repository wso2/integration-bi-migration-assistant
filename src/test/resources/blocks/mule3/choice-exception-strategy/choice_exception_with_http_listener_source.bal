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
        do {
            log:printInfo("xxx: logger invoked via http end point");
        } on fail error e {
            if "condition1" {
                log:printInfo("xxx: first catch condition invoked");
            } else if "condition2" {
                log:printInfo("xxx: second catch condition invoked");
            } else {
                log:printInfo("xxx: generic catch condition invoked");
            }
        }
        return ctx.inboundProperties.response;
    }
}
