import ballerina/log;

public type SessionVars record {|
    string sessionVar1?;
|};

public type Context record {|
    anydata payload;
    SessionVars sessionVars;
|};

public function myFlow(Context ctx) {
    log:printInfo("xxx: flow starting logger invoked");
    ctx.sessionVars.sessionVar1 = "initial value";
    ctx.sessionVars.sessionVar1 = "updated value";
    log:printInfo("xxx: end of flow reached");
}
