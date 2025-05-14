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

public listener http:Listener config = new (8081, {host: "localhost"});

service /mule3 on config {
    Context ctx;

    function init() {
        self.ctx = {payload: (), inboundProperties: {response: new, request: new, uriParams: {}}};
    }

    resource function get demo(http:Request request) returns http:Response|error {
        self.ctx.inboundProperties.request = request;
        self.ctx.inboundProperties.response = new;
        return invokeEndPoint0(self.ctx);
    }
}

public function invokeEndPoint0(Context ctx) returns http:Response|error {
    log:printInfo("xxx: logger invoked");

    ctx.inboundProperties.response.setPayload(ctx.payload);
    return ctx.inboundProperties.response;
}
