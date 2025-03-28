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

    resource function post .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    resource function put .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    resource function delete .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    resource function patch .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    resource function head .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    resource function options .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    resource function trace .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    resource function connect .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    private function _invokeEndPoint0_(Context ctx) returns http:Response|error {
        log:printInfo("xxx: logger invoked");
        return ctx.inboundProperties.response;
    }
}
