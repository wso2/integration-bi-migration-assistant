import ballerina/http;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /mule3 on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;

        // set payload
        string _payload0_ = "First payload";

        // set payload
        string _payload1_ = "Second payload";

        // set payload
        string _payload2_ = "Third payload";
        _response_.setPayload(_payload2_);
        return _response_;
    }
}
