import ballerina/http;
import ballerina/log;

listener http:Listener httpConfig = new (8081, {host: "0.0.0.0"});

function commonConfig1Flow(Context ctx) {
    log:printInfo("xxx: common config1 logger invoked");
}
