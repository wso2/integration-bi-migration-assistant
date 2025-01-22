import ballerina/http;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /foo on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0();
    }

    resource function post .() returns http:Response|error {
        return self._invokeEndPoint0();
    }

    resource function put .() returns http:Response|error {
        return self._invokeEndPoint0();
    }

    resource function delete .() returns http:Response|error {
        return self._invokeEndPoint0();
    }

    private function _invokeEndPoint0() returns http:Response|error {
        http:Response _response_ = new;
        string _dwOutput = "Hello World";
        _response_.setPayload(_dwOutput);
        return _response_;
    }
}
