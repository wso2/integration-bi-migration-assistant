import ballerina/log;

public type Context record {|
    anydata payload = ();
|};

public function mainFlow(Context ctx) returns error? {

    // set payload
    string payload0 = "Hello";
    ctx.payload = payload0;

    // async operation
    _ = start async0(ctx);
    log:printInfo("Main flow continues immediately.");
}

public function async0(Context ctx) returns error? {
    log:printInfo(string `Doing something in background: ${ctx.payload.toString()}`);
}
