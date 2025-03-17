import ballerina/log;

function muleProject() {
    if ("condition1") {
        log:printInfo("xxx: first when condition invoked");
    } else if ("condition2") {
        log:printInfo("xxx: second when condition invoked");
    } else if ("condition3") {
        log:printInfo("xxx: third when condition invoked");
    } else {
        log:printInfo("xxx: default condition invoked");
    }
}
