import ballerina/http;
import ballerina/log;

public type FlowVars record {|
    int marks?;
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
        return ctx.inboundProperties.response;
    }
}
