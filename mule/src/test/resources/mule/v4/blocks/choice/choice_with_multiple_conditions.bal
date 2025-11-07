import ballerina/log;

public type Vars record {|
    int marks?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function demoFlow(Context ctx) {
    ctx.vars.marks = 73;
    if ctx.vars?.marks >= 75 {
        log:printInfo(string `You have scored ${ctx.vars?.marks.toString()}. Your grade is 'A'.`);
    } else if ctx.vars?.marks >= 65 {
        log:printInfo(string `You have scored ${ctx.vars?.marks.toString()}. Your grade is 'B'.`);
    } else if ctx.vars?.marks >= 55 {
        log:printInfo(string `You have scored ${ctx.vars?.marks.toString()}. Your grade is 'C'.`);
    } else {
        log:printInfo(string `You have scored ${ctx.vars?.marks.toString()}. Your grade is 'F'.`);
    }
}
