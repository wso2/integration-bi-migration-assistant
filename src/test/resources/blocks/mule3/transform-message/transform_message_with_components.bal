import ballerina/http;

public type InboundProperties record {|
    http:Response response;
|};

public type Context record {|
    anydata payload;
    InboundProperties inboundProperties;
|};

public listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /foo on config {
    Context ctx;

    function init() {
        self.ctx = {payload: (), inboundProperties: {response: new}};
    }

    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    private function _invokeEndPoint0_(Context ctx) returns http:Response|error {
        json myVariable = _dwMethod0_(ctx.payload.toJson());
        json _dwOutput_ = _dwMethod0_(ctx.payload.toJson());
        json mySessionVariable = _dwMethod0_(ctx.payload.toJson());
        ctx.inboundProperties.response.setPayload(_dwOutput_);
        return ctx.inboundProperties.response;
    }
}

function _dwMethod0_(json payload) returns json {
    return "apple".toUpperAscii();
}
