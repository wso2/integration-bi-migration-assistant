import ballerina/http;
import ballerina/log;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /mule3 on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;
        if ("condition1") {
            log:printInfo("xxx: first when condition invoked");
        } else if ("condition2") {
            log:printInfo("xxx: second when condition invoked");
        } else if ("condition3") {
            log:printInfo("xxx: third when condition invoked");
        } else {
            log:printInfo("xxx: default condition invoked");
        }
        return _response_;
    }
}
