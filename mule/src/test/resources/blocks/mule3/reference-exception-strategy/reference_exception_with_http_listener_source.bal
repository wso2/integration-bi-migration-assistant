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

public listener http:Listener httpConfig = new (8081);

service / on httpConfig {
    Context ctx;

    function init() {
        self.ctx = {payload: (), inboundProperties: {response: new, request: new, uriParams: {}}};
    }

    resource function get .(http:Request request) returns http:Response|error {
        self.ctx.inboundProperties.request = request;
        return invokeEndPoint0(self.ctx);
    }
}

public function catch\-exception\-strategy(Context ctx, error e) {
    log:printInfo("xxx: inside catch exception strategy");
}

public function invokeEndPoint0(Context ctx) returns http:Response|error {
    do {
        log:printInfo("xxx: end of flow reached");
    } on fail error e {
        catch\-exception\-strategy(ctx, e);
    }

    ctx.inboundProperties.response.setPayload(ctx.payload);
    return ctx.inboundProperties.response;
}
