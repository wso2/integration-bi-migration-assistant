import ballerina/log;

public type Context record {|
    anydata payload = ();
|};

public function vmListenerFlow(Context ctx) returns error? {
    log:printInfo(string `Received a message: ${ctx.payload.toString()}`);
}

public function vmPublishFlow(Context ctx) returns error? {
    worker W returns error? {
        // VM Listener
        anydata receivedPayload = <- function;
        ctx.payload = receivedPayload;
        check vmListenerFlow(ctx);
    }

    // set payload
    string payload0 = "Hello World!";
    ctx.payload = payload0;

    // VM Publish
    ctx.payload -> W;
}
