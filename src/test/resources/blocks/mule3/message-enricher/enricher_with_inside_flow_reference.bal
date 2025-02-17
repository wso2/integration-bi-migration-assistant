import ballerina/log;

function variableEnricherFlow() {
    string userId = "st455u";
    string enrichedUserId = "null";
    enrichedUserId = _enricher0_(userId);
    log:printInfo(string `User ID: ${flowVars.userId}, Enriched User ID: ${flowVars.enrichedUserId}`);
}

function flow1() {
    log:printInfo("xxx: flow1 starting logger invkoed");
    log:printInfo("xxx: end of flow1 reached");
}

function _enricher0_(string userId) returns string {
    flow1();
    return userId;
}
