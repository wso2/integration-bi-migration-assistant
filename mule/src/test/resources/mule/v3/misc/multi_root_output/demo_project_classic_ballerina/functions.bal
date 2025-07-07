import ballerina/log;

function _dwMethod0_(json payload) returns json|error {
    float conversionRate = 13.15;
    return {"s": "Hello World", "n": 1.23, "b": true, "a": check [1, 2, 3].ensureType(json), "o": check {"name": "Anne"}.ensureType(json)};
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
