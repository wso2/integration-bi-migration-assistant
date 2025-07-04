import ballerina/log;

function _dwMethod1_(json payload) returns json|error {
    float conversionRate = 13.15;
    return {"s": "Hello World", "n": 1.23, "b": true, "a": check [1, 2, 3].ensureType(json), "o": check {"name": "Anne"}.ensureType(json)};
}

function _dwMethod2_(xml payload) returns xml {
    //TODO: UNSUPPORTED DATAWEAVE EXPRESSION 'map$+1' OF TYPE 'xml' FOUND. MANUAL CONVERSION REQUIRED.
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

public function commonConfig2Sub_Flow(Context ctx) {
    log:printInfo("xxx: common config2 logger invoked");
}

public function commonConfig1Flow(Context ctx) {
    log:printInfo("xxx: common config1 logger invoked");
}

public function mainconfigSub_Flow(Context ctx) {
    log:printInfo("xxx: main config logger invoked");
}
