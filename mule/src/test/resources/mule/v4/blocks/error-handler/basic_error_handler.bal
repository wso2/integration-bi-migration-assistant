import ballerina/log;

public type Context record {|
    anydata payload = ();
|};

public function demoFlow(Context ctx) {
    do {

        // set payload
        anydata payload0 = 1 / 0;
        ctx.payload = payload0;
        log:printInfo("xxx: log after exception");
    } on fail error err {
        my_error_handler(ctx, err);
    }
}

public function my_error_handler(Context ctx, error e) {
    // on-error-propagate
    log:printError("Message: " + err.message());
    log:printError("Trace: " + err.stackTrace().toString());

    log:printInfo("Error handled in on-error-propagate");

    // set payload
    string payload1 = "Custom error message: Something went wrong.";
    ctx.payload = payload1;
    panic err;
}
