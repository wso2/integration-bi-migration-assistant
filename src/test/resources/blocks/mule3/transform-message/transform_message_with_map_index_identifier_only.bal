import ballerina/http;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /foo on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;
        json _dwOutput_ = _dwMethod0_(payload);
        _response_.setPayload(_dwOutput_);
        return _response_;
    }
}

function _dwMethod0_(json payload) returns json {
    var arrayArg = [1, 2, 3, 4];
    return arrayArg.'map(element => arrayArg.indexOf(element) + 1);
}
