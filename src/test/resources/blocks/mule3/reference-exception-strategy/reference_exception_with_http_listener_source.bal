import ballerina/http;
import ballerina/log;

public type InboundProperties record {|
    http:Response response;
|};

public type Context record {|
    anydata payload;
    InboundProperties inboundProperties;
|};

public listener http:Listener httpConfig = new (8081, {host: "0.0.0.0"});

service / on httpConfig {
    Context ctx;

    function init() {
        self.ctx = {payload: (), inboundProperties: {response: new}};
    }

    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    private function _invokeEndPoint0_(Context ctx) returns http:Response|error {
        do {
            log:printInfo("xxx: end of flow reached");
        } on fail error e {
            catch\-exception\-strategy(ctx, e);
        }
        return ctx.inboundProperties.response;
    }
}

public function catch\-exception\-strategy(Context ctx, error e) {
    log:printInfo("xxx: inside catch exception strategy");
}
