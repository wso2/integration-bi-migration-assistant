import ballerina/http;
import ballerina/log;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /mule3 on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;
        log:printInfo("xxx: INFO level logger invoked");
        log:printDebug("xxx: DEBUG level logger invoked");
        log:printError("xxx: ERROR level logger invoked");
        log:printWarn("xxx: WARN level logger invoked");
        log:printInfo("xxx: TRACE level logger invoked");
        return _response_;
    }
}
