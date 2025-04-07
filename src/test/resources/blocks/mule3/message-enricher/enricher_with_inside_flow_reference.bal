import ballerina/log;

type FlowVars record {|
    string userId?;
    string enrichedUserId?;
|};

type Context record {|
    anydata payload;
    FlowVars flowVars;
|};

function flow1(Context ctx) {
    log:printInfo("xxx: flow1 starting logger invkoed");
    log:printInfo("xxx: end of flow1 reached");
}

function _enricher0_(Context ctx) returns string? {
    flow1(ctx);
    return ctx.flowVars.userId;
}

function variableEnricherFlow(Context ctx) {
    ctx.flowVars.userId = "st455u";
    ctx.flowVars.enrichedUserId = "null";
    ctx.flowVars.enrichedUserId = _enricher0_(ctx.clone());
    log:printInfo(string `User ID: ${ctx.flowVars.userId.toString()}, Enriched User ID: ${ctx.flowVars.enrichedUserId.toString()}`);
}
