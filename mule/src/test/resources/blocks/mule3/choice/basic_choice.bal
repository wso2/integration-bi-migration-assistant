import ballerina/log;

public type FlowVars record {|
    int age?;
|};

public type Context record {|
    anydata payload;
    FlowVars flowVars;
|};

public function muleProject(Context ctx) {
    ctx.flowVars.age = 29;
    if ctx.flowVars.age > 18 {
        log:printInfo(string `Adult detected: Age is ${ctx.flowVars.age.toString()} years.`);
    } else {
        log:printInfo("Minor detected: Age is flowVars.age years.");
    }
}
