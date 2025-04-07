import ballerina/log;

type Context record {|
    anydata payload;
|};

function demoFlow(Context ctx) {
    do {
        log:printInfo("xxx: logger invoked via http end point");
    } on fail {
        log:printInfo("xxx: exception caught");
        log:printInfo("xxx: end of catch flow reached");
    }
}
