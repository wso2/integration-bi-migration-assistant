import ballerina/log;

public function muleprojectdemoFlow(Context ctx) returns error? {
    log:printInfo("Before calling test flow");
    check lib:TestFlow(ctx);
    log:printInfo("Returned from test flow");
}
