import ballerina/log;

public type Vars record {|
    int age?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function demoFlow(Context ctx) {
    ctx.vars.age = 29;
    if ctx.vars.age > 18 {
        log:printInfo(string `Adult detected: Age is ${ctx.vars.age.toString()} years.`);
    } else {
        log:printInfo(string `Minor detected: Age is ${ctx.vars.age.toString()} years.`);
    }
}
