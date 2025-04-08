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
        string _payload0_ = "First payload";
        ctx.payload = _payload0_;

        // set payload
        string _payload1_ = "Second payload";
        ctx.payload = _payload1_;

        // set payload
        string _payload2_ = "Third payload";
        ctx.payload = _payload2_;
        ctx.inboundProperties.response.setPayload(_payload2_);
        return ctx.inboundProperties.response;
    }
}
