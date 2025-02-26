import ballerina/http;

public type FlowVars record {|
    string name?;
    string age?;
|};

public type InboundProperties record {|
    http:Response response;
|};

public type Context record {|
    anydata payload;
    FlowVars flowVars;
    InboundProperties inboundProperties;
|};

public listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /mule3 on config {
    Context ctx;

    function init() {
        self.ctx = {payload: (), flowVars: {}, inboundProperties: {response: new}};
    }

    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    private function _invokeEndPoint0_(Context ctx) returns http:Response|error {
        ctx.flowVars.name = "lochana";
        ctx.flowVars.age = "29";
        return ctx.inboundProperties.response;
    }
}
