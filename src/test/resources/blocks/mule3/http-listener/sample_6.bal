import ballerina/http;
import ballerina/log;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /mule3 on config {
    resource function get [string version]/demo/[string id]() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;
        log:printInfo("xxx: logger invoked");
        return _response_;
    }
}
