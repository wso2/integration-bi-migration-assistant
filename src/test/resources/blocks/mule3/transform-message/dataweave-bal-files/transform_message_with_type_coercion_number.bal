import ballerina/http;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /foo on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;
        json _dwOutput_ = check _dwMethod0_();
        _response_.setPayload(_dwOutput_);
        return _response_;
    }
}

function _dwMethod0_() returns json|error {
    return check int:fromString("10");
}
