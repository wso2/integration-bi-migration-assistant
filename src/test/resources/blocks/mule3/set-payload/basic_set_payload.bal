import ballerina/http;

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

        // set payload
        string _payload0_ = "Hello world!";
        ctx.payload = _payload0_;
        ctx.inboundProperties.response.setPayload(_payload0_);
        return ctx.inboundProperties.response;
    }
}
