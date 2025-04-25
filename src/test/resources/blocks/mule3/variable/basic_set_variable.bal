import ballerina/http;

public type FlowVars record {|
    string name?;
    string age?;
|};

public type InboundProperties record {|
    http:Response response;
    http:Request request;
    map<string> uriParams;
|};

public type Context record {|
    anydata payload;
    FlowVars flowVars;
    InboundProperties inboundProperties;
|};

public listener http:Listener config = new (8081);

service /mule3 on config {
    Context ctx;

    function init() {
        self.ctx = {payload: (), flowVars: {}, inboundProperties: {response: new, request: new, uriParams: {}}};
    }

    resource function get .(http:Request request) returns http:Response|error {
        self.ctx.inboundProperties.request = request;
        return _invokeEndPoint0_(self.ctx);
    }
}

public function _invokeEndPoint0_(Context ctx) returns http:Response|error {
    ctx.flowVars.name = "lochana";
    ctx.flowVars.age = "29";

    ctx.inboundProperties.response.setPayload(ctx.payload);
    return ctx.inboundProperties.response;
}
