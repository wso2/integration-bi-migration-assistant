import ballerina/http;
import ballerina/test;

// The service that routes requests to the operations of test.raml
final http:Client apikitConfigClient = check new (string `http://localhost:${http\-listener\-config.getPort()}`);
// The service that routes requests to the operations of test.raml
final http:Client partnerApikitConfigClient = check new (string `http://localhost:${http\-listener\-config.getPort()}`);

@test:Config {groups: ["api-spec"]}
function testApikitConfigAnswersNotFoundToUnknownPath() returns error? {
    http:Response response = check apikitConfigClient->get("/api/api-spec-test/no/such/path");
    test:assertEquals(response.statusCode, 404);
}

@test:Config {groups: ["api-spec"]}
function testPartnerApikitConfigAnswersNotFoundToUnknownPath() returns error? {
    http:Response response = check partnerApikitConfigClient->get("/partner/api-spec-test/no/such/path");
    test:assertEquals(response.statusCode, 404);
}
