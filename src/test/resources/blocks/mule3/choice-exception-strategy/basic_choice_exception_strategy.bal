import ballerina/log;

function demoFlow() {
    do {
        log:printInfo("xxx: main flow logger invoked");
    } on fail error e {
        if condition1 {
            log:printInfo("xxx: first catch condition invoked");
        } else if condition2 {
            log:printInfo("xxx: second catch condition invoked");
        } else {
            log:printInfo("xxx: generic catch condition invoked");
        }
    }
}
