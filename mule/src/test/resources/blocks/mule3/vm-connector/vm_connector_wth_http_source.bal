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
        return invokeEndPoint0(self.ctx);
    }
}

public function invokeEndPoint0(Context ctx) returns http:Response|error {
    worker W returns error? {
        // VM Inbound Endpoint
        anydata receivedPayload = <- function;
        ctx.payload = receivedPayload;
        vmReceive0(ctx);
    }

    // set payload
    string payload0 = "Hello World";
    ctx.payload = payload0;

    // VM Outbound Endpoint
    ctx.payload -> W;

    ctx.inboundProperties.response.setPayload(ctx.payload);
    return ctx.inboundProperties.response;
}

public function vmReceive0(Context ctx) {
    log:printInfo(string `Received a message: ${ctx.payload.toString()}`);
}
