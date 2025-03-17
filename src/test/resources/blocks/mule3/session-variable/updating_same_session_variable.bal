import ballerina/log;

string sessionVar1 = "initial value";

function myFlow() {
    log:printInfo("xxx: flow starting logger invoked");
    sessionVar1 = "updated value";
    log:printInfo("xxx: end of flow reached");
}
