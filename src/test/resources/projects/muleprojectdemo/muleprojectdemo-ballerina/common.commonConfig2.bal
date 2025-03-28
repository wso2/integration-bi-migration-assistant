import ballerina/http;
import ballerina/log;

listener http:Listener httpConfig = new (8081, {host: "0.0.0.0"});

function commonConfig2Sub_Flow(Context ctx) {
    log:printInfo("xxx: common config2 logger invoked");
}
