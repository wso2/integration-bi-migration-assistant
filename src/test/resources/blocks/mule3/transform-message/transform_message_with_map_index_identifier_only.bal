import ballerina/http;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /foo on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function post .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function put .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function delete .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function patch .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function head .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function options .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function trace .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    resource function connect .() returns http:Response|error {
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
    json[] arrayArg = <json[]>[1, 2, 3, 4];
    // TODO: AMBIGUOUS TYPE FOUND FOR MATH OPERATOR '$$+1'. MANUAL CASTING REQUIRED.
    return arrayArg.'map(element => <int>arrayArg.indexOf(element) + <int>1);
}
