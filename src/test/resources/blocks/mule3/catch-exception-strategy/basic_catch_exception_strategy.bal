import ballerina/log;

function demoFlow() {
    do {
        log:printInfo("xxx: logger invoked via http end point");
    } on fail {
        log:printInfo("xxx: exception caught");
        log:printInfo("xxx: end of catch flow reached");
    }
}
