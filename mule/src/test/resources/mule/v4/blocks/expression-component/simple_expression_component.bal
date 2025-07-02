import ballerina/log;

public type FlowVars record {|
    string name?;
|};

public type Context record {|
    anydata payload = ();
    FlowVars flowVars = {};
|};

public function combineFlowVarsAndPayloadFlow(Context ctx) {
    ctx.flowVars.name = "Alice";

    // set payload
    string payload0 = "Welcome";
    ctx.payload = payload0;
    ctx.flowVars.name = "Alice";
    ctx.payload = "Hello " + ctx.flowVars.name;
    log:printInfo(string `Message: ${ctx.payload.toString()}`);
}
