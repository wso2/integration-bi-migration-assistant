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
        // on-error-continue
        log:printError("Message: " + e.message());
        log:printError("Trace: " + e.stackTrace().toString());

        log:printInfo("xxx: error handled in on-error-continue");

        // set payload
        string payload1 = "Default value: Error occurred but we handled it.";
        ctx.payload = payload1;
    }
}
