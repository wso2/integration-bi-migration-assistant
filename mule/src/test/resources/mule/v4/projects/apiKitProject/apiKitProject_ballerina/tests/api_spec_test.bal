import ballerina/http;
import ballerina/test;

// The service that routes requests to the operations of test.raml
final http:Client apikitConfigClient = check new (string `http://localhost:${http\-listener\-config.getPort()}`);

@test:Config {groups: ["api-spec"]}
function testGetOrdersAnswersNotImplemented() returns error? {
    http:Response response = check apikitConfigClient->get("/api/orders?id=test");
    test:assertEquals(response.statusCode, 501);
}

@test:Config {groups: ["api-spec"]}
function testApikitConfigAnswersNotFoundToUnknownPath() returns error? {
    http:Response response = check apikitConfigClient->get("/api/api-spec-test/no/such/path");
    test:assertEquals(response.statusCode, 404);
}
