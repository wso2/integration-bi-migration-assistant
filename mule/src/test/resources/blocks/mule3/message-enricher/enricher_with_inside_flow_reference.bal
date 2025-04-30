import ballerina/log;

public type FlowVars record {|
    string userId?;
    string enrichedUserId?;
|};

public type Context record {|
    anydata payload;
    FlowVars flowVars;
|};

public function variableEnricherFlow(Context ctx) {
    ctx.flowVars.userId = "st455u";
    ctx.flowVars.enrichedUserId = "null";
    ctx.flowVars.enrichedUserId = enricher0(ctx.clone());
    log:printInfo(string `User ID: ${ctx.flowVars.userId.toString()}, Enriched User ID: ${ctx.flowVars.enrichedUserId.toString()}`);
}

public function enricher0(Context ctx) returns string? {
    flow1(ctx);
    return ctx.flowVars.userId;
}

public function flow1(Context ctx) {
    log:printInfo("xxx: flow1 starting logger invkoed");
    log:printInfo("xxx: end of flow1 reached");
}
