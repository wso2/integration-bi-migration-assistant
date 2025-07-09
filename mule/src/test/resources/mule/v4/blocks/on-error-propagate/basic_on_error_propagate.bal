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
    } on fail error e {
        // on-error-propagate
        log:printError("Message: " + e.message());
        log:printError("Trace: " + e.stackTrace().toString());

        log:printInfo("Error handled in on-error-propagate");

        // set payload
        string payload1 = "Custom error message: Something went wrong.";
        ctx.payload = payload1;
        panic e;
    }
}
