import ballerina/http;
import ballerina/log;

listener http:Listener httpConfig = new (8081, {host: "0.0.0.0"});

service / on httpConfig {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;
        do {
            log:printInfo("xxx: end of flow reached");
        } on fail error e {
            catch\-exception\-strategy(e);
        }
        return _response_;
    }
}

function catch\-exception\-strategy(error e) {
    log:printInfo("xxx: inside catch exception strategy");
}
