import ballerina/http;
import ballerina/log;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /mule3 on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function post .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function put .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function delete .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function patch .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function head .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function options .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function trace .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function connect .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;
        log:printInfo("xxx: logger invoked");
        return _response_;
    }
}
