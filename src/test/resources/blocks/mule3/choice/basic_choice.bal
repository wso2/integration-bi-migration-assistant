import ballerina/log;

function muleProject() {
    if "condition" {
        log:printInfo("xxx: when condition invoked");
    } else {
        log:printInfo("xxx: default condition invoked");
    }
}
