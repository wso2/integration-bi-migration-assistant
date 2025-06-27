import ballerina/log;

public type SessionVars record {|
    string day?;
    string month?;
    string 'from?;
|};

public type Context record {|
    anydata payload = ();
    SessionVars sessionVars = {};
|};

public function myFlow(Context ctx) {
    log:printInfo("xxx: flow starting logger invoked");
    ctx.sessionVars.day = "21";
    ctx.sessionVars.month = "July";
    ctx.sessionVars.'from = "2025";
    log:printInfo(string `Session variables are: day - ${ctx.sessionVars.day.toString()}, month - ${ctx.sessionVars.month.toString()}, from - ${ctx.sessionVars.'from.toString()}`);
}
