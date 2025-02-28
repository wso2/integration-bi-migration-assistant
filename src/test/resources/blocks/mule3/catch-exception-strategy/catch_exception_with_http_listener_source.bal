import ballerina/http;
import ballerina/log;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /mule3 on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;
        do {
            log:printInfo("xxx: logger invoked via http end point");
        } on fail {
            log:printInfo("xxx: exception caught");
            log:printInfo("xxx: end of catch flow reached");
        }
        return _response_;
    }
}
