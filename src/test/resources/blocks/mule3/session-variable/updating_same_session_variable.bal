import ballerina/log;

type SessionVars record {|
    string sessionVar1?;
|};

type Context record {|
    anydata payload;
    SessionVars sessionVars;
|};

function myFlow(Context ctx) {
    log:printInfo("xxx: flow starting logger invoked");
    ctx.sessionVars.sessionVar1 = "initial value";
    ctx.sessionVars.sessionVar1 = "updated value";
    log:printInfo("xxx: end of flow reached");
}
