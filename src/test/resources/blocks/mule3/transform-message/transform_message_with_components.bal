import ballerina/http;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /foo on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;
        json myVariable = _dwMethod0_(payload);
        json _dwOutput_ = _dwMethod1_(payload);
        json mySessionVariable = _dwMethod2_(payload);
        _response_.setPayload(_dwOutput_);
        return _response_;
    }
}

function _dwMethod0_(json payload) returns json {
    return "apple".toUpperAscii();
}

function _dwMethod2_(json payload) returns json {
    return "apple".toUpperAscii();
}

function _dwMethod1_(json payload) returns json {
    return "apple".toUpperAscii();
}
