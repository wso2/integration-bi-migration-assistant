import ballerina/http;
import ballerina/log;

listener http:Listener httpConfig = new (8081, {host: "0.0.0.0"});

function mainconfigSub_Flow(Context ctx) {
    log:printInfo("xxx: main config logger invoked");
}
