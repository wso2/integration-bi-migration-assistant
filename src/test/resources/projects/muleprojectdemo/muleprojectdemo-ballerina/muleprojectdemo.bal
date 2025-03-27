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
        json scriptVar = check _dwMethod0_(payload);
        json inlineVar = check _dwMethod1_(payload);
        json _dwOutput_ = check _dwMethod1_(payload);
        _response_.setPayload(_dwOutput_);
        log:printInfo("xxx: end of the logger reached");
        return _response_;
    }
}

function _dwMethod1_(json payload) returns json|error {
    float conversionRate = 13.15;
    return {"s": "Hello World", "n": 1.23, "b": true, "a": check [1, 2, 3].ensureType(json), "o": check {"name": "Anne"}.ensureType(json)};
}

function _dwMethod0_(json payload) returns json|error {
    json _var_0;
    if check payload.country == "USA" {
        _var_0 = {"currency": "USD"};
    } else if check payload.country == "UK" {
        _var_0 = {"currency": "GBP"};
    } else {
        _var_0 = {"currency": "EUR"};
    }
    return _var_0;
}
