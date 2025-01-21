import ballerina/http;
import ballerina/log;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /foo on config {
    resource function get () returns http:Response|error {
        return self._invokeEndPoint0();
    }

    private function _invokeEndPoint0() returns http:Response|error {
        http:Response _response_ = new;
        log:printInfo("xxx: this is 'INFO' level message log");
        log:printDebug("xxx: this is 'DEBUG' level message log");
        log:printError("xxx: this is 'ERROR' level message log");
        log:printInfo("xxx: this is 'TRACE' level message log");
        log:printWarn("xxx: this is 'WARN' level message log");
        return _response_;
    }
}
