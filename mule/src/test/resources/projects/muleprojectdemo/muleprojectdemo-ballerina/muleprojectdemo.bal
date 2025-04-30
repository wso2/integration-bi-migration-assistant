import ballerina/http;
import ballerina/log;

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

function _dwMethod1_(json payload) returns json|error {
    float conversionRate = 13.15;
    return {"s": "Hello World", "n": 1.23, "b": true, "a": check [1, 2, 3].ensureType(json), "o": check {"name": "Anne"}.ensureType(json)};
}

public function invokeEndPoint0(Context ctx) returns http:Response|error {
    mainconfigSub_Flow(ctx);
    commonConfig1Flow(ctx);
    commonConfig2Sub_Flow(ctx);
    json scriptVar = check _dwMethod0_(ctx.payload.toJson());
    json inlineVar = check _dwMethod1_(ctx.payload.toJson());
    xml _dwOutput_ = _dwMethod2_(ctx.payload.toJson());
    ctx.payload = _dwOutput_;
    log:printInfo("xxx: end of the logger reached");

    ctx.inboundProperties.response.setPayload(ctx.payload);
    return ctx.inboundProperties.response;
}

function _dwMethod2_(xml payload) returns xml {
    //TODO: UNSUPPORTED DATAWEAVE EXPRESSION 'map$+1' OF TYPE 'xml' FOUND. MANUAL CONVERSION REQUIRED.
}

function _dwMethod0_(json payload) returns json|error {
    json _var_0;
    if check payload.country == "USA" {
        _var_0 = {"currency": "USD"};
    } else if check payload.country == "UK" {
        _var_0 = {"currency": "GBP"};
    } else {
        _var_0 = {"currency": "EUR"};
    }
    return _var_0;
}
