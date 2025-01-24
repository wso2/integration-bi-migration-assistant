import ballerina/http;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /mule3 on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0();
    }

    private function _invokeEndPoint0() returns http:Response|error {
        http:Response _response_ = new;
        _response_.setPayload("First payload");
        _response_.setPayload("Second payload");
        _response_.setPayload("Third payload");
        return _response_;
    }
}
