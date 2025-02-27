import ballerina/http;
import ballerina/log;

listener http:Listener httpConfig = new (8081, {host: "0.0.0.0"});

service / on httpConfig {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;
        mainconfigSub_Flow(_response_);
        commonConfig1Flow(_response_);
        commonConfig2Sub_Flow(_response_);
        log:printInfo("xxx: end of the logger reached");
        return _response_;
    }
}
