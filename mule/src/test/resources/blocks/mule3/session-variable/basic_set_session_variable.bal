import ballerina/log;

public type SessionVars record {|
    string year?;
    string month?;
|};

public type Context record {|
    anydata payload = ();
    SessionVars sessionVars = {};
|};

public function myFlow(Context ctx) {
    log:printInfo("xxx: flow starting logger invoked");
    ctx.sessionVars.year = "2025";
    ctx.sessionVars.month = "July";
    log:printInfo(string `Session variables are: year - ${ctx.sessionVars.year.toString()}, month - ${ctx.sessionVars.month.toString()}`);
}
