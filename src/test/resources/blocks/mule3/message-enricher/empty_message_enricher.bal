import ballerina/log;

function variableEnricherFlow() {
    string userId = "st455u";
    string enrichedUserId = "null";
    enrichedUserId = userId;
    log:printInfo(string `User ID: ${flowVars.userId}, Enriched User ID: ${flowVars.enrichedUserId}`);
}
