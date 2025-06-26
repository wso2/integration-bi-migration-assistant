import ballerina/log;

public function commonConfig2Sub_Flow(Context ctx) {
    log:printInfo("xxx: common config2 logger invoked");
}

public function commonConfig1Flow(Context ctx) {
    log:printInfo("xxx: common config1 logger invoked");
}

public function mainconfigSub_Flow(Context ctx) {
    log:printInfo("xxx: main config logger invoked");
}
