import ballerina/http;
import ballerina/log;

public type Context record {|
    anydata payload = ();
|};

public function demoFlow(Context ctx) {

    // set payload
    string payload0 = "John";
    ctx.payload = payload0;

    // async operation
    _ = start async0(ctx);
    log:printInfo(string `Payload is '${ctx.payload.toString()}'`);
}

public function async0(Context ctx) {

    // set payload
    string payload1 = "Harry";
    ctx.payload = payload1;
    log:printInfo(string `Payload updated to: ${ctx.payload.toString()}`);
}
