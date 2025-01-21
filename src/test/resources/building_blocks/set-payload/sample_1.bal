import ballerina/http;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /foo on config {
    resource function get () returns http:Response|error {
        return self._invokeEndPoint0();
    }

    private function _invokeEndPoint0() returns http:Response|error {
        http:Response _response_ = new;
        _response_.setPayload(Hello World!);
        return _response_;
    }
}
