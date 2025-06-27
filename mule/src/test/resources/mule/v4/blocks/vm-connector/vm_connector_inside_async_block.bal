import ballerina/http;
import ballerina/log;

public type InboundProperties record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    InboundProperties inboundProperties;
|};

public listener http:Listener HTTP_Listener_Configuration = new (8081);

service / on HTTP_Listener_Configuration {
    resource function get vm(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};

        // set payload
        string payload0 = "Hello World";
        ctx.payload = payload0;

        // async operation
        _ = start async0(ctx);
        log:printInfo("xxx: logger after async block invoked");

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}

public function vmReceive0(Context ctx) {
    log:printInfo(string `Received a message: ${ctx.payload.toString()}`);
}

public function async0(Context ctx) {
    worker W returns error? {
        // VM Inbound Endpoint
        anydata receivedPayload = <- function;
        ctx.payload = receivedPayload;
        vmReceive0(ctx);
    }

    // VM Outbound Endpoint
    ctx.payload -> W;
}
