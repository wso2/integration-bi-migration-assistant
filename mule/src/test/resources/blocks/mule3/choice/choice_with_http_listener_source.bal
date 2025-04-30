import ballerina/http;
import ballerina/log;

public type FlowVars record {|
    int marks?;
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
        return invokeEndPoint0(self.ctx);
    }
}

public function invokeEndPoint0(Context ctx) returns http:Response|error {
    ctx.flowVars.marks = 73;
    if ctx.flowVars.marks > 75 {
        log:printInfo(string `You have scored ${ctx.flowVars.marks.toString()}. Your grade is A.`);
    } else if ctx.flowVars.marks > 65 {
        log:printInfo(string `You have scored ${ctx.flowVars.marks.toString()}. Your grade is B.`);
    } else if ctx.flowVars.marks > 55 {
        log:printInfo(string `You have scored ${ctx.flowVars.marks.toString()}. Your grade is C.`);
    } else {
        log:printInfo(string `You have scored ${ctx.flowVars.marks.toString()}. Your grade is F.`);
    }

    ctx.inboundProperties.response.setPayload(ctx.payload);
    return ctx.inboundProperties.response;
}
