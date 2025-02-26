import ballerina/log;

public type Context record {|
    anydata payload;
|};

public function mainFlow(Context ctx) {

    // set payload
    string _payload0_ = "Hello";
    ctx.payload = _payload0_;

    // async operation
    _ = start _async0_(ctx);
    log:printInfo("Main flow continues immediately.");
}

public function _async0_(Context ctx) {
    log:printInfo(string `Doing something in background: ${ctx.payload.toString()}`);
}
