import ballerina/log;

string sessionVar1 = "this is first session variable";
string sessionVar2 = "this is second session variable";

function myFlow() {
    log:printInfo("xxx: flow starting logger invoked");
    log:printInfo("xxx: end of flow reached");
}
