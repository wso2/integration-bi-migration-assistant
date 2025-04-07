import ballerina/log;

type FlowVars record {|
    int marks?;
|};

type Context record {|
    anydata payload;
    FlowVars flowVars;
|};

function muleProject(Context ctx) {
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
}
