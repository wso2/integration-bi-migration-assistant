import ballerina/log;

public function muleprojectdemoFlow(Context ctx) {
    log:printInfo("Before calling test flow");
    lib:TestFlow(ctx);
    log:printInfo("Returned from test flow");
}
