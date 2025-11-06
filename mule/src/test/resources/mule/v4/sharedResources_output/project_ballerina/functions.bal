import ballerina/log;

public function muleprojectdemoFlow(Context ctx) {
    log:printInfo("Before calling test flow");
    lib_ballerina:TestFlow(ctx);
    log:printInfo("Returned from test flow");
}
