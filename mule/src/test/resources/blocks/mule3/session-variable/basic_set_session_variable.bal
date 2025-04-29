import ballerina/log;

public type SessionVars record {|
    string sessionVar1?;
    string sessionVar2?;
|};

public type Context record {|
    anydata payload;
    SessionVars sessionVars;
|};

public function myFlow(Context ctx) {
    log:printInfo("xxx: flow starting logger invoked");
    ctx.sessionVars.sessionVar1 = "this is first session variable";
    ctx.sessionVars.sessionVar2 = "this is second session variable";
    log:printInfo("xxx: end of flow reached");
}
