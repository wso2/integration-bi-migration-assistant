import ballerina/http;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /mule3 on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;

        // set payload
        string _payload0_ = "Hello world!";
        _response_.setPayload(_payload0_);
        return _response_;
    }
}
