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

public listener http:Listener HTTP_Listener_Configuration = new (8081);

service / on HTTP_Listener_Configuration {
    Context ctx;

    function init() {
        self.ctx = {payload: (), inboundProperties: {response: new, request: new, uriParams: {}}};
    }

    resource function get vm(http:Request request) returns http:Response|error {
        self.ctx.inboundProperties.request = request;
        return _invokeEndPoint0_(self.ctx);
    }
}

public function _invokeEndPoint0_(Context ctx) returns http:Response|error {

    // set payload
    string _payload0_ = "Hello World";
    ctx.payload = _payload0_;

    // async operation
    _ = start _async0_(ctx);
    log:printInfo("xxx: logger after async block invoked");

    ctx.inboundProperties.response.setPayload(ctx.payload);
    return ctx.inboundProperties.response;
}

public function _vmReceive0_(Context ctx) {
    log:printInfo(string `Received a message: ${ctx.payload.toString()}`);
}

public function _async0_(Context ctx) {
    worker W returns error? {
        // VM Inbound Endpoint
        anydata receivedPayload = <- function;
        ctx.payload = receivedPayload;
        _vmReceive0_(ctx);
    }

    // VM Outbound Endpoint
    ctx.payload -> W;
}
