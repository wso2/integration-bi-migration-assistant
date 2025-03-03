import ballerina/log;

function variableEnricherFlow() {
    string userId = "st455u";
    string enrichedUserId = "null";
    enrichedUserId = _enricher0_(userId);
    log:printInfo(string `User ID: ${flowVars.userId}, Enriched User ID: ${flowVars.enrichedUserId}`);
}

function _enricher0_(string userId) returns string {
    log:printInfo("xxx: logger inside the message enricher invoked");
    return userId;
}
