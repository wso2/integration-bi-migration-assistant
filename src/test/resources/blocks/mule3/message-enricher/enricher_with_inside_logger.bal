import ballerina/log;

type FlowVars record {|
    string userId?;
    string enrichedUserId?;
|};

type Context record {|
    anydata payload;
    FlowVars flowVars;
|};

function variableEnricherFlow(Context ctx) {
    ctx.flowVars.userId = "st455u";
    ctx.flowVars.enrichedUserId = "null";
    ctx.flowVars.enrichedUserId = _enricher0_(ctx.clone());
    log:printInfo(string `User ID: ${ctx.flowVars.userId.toString()}, Enriched User ID: ${ctx.flowVars.enrichedUserId.toString()}`);
}

function _enricher0_(Context ctx) returns string? {
    log:printInfo("xxx: logger inside the message enricher invoked");
    return ctx.flowVars.userId;
}
