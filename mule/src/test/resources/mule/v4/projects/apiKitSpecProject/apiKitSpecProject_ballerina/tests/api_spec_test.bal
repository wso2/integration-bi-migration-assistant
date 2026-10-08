import ballerina/constraint;
import ballerina/http;
import ballerina/test;

// The service that routes requests to the operations of orders-api.raml
final http:Client ordersConfigClient = check new (string `http://localhost:${http\-listener\-config.getPort()}`);
final json postOrdersRequestExample = check string `{
  "orderId" : "2002",
  "status" : "NEW",
  "quantity" : 3,
  "addresses" : [ {
    "street" : "High St"
  } ]
}`.fromJsonString();
final json postOrdersOrderIdNotesRequestExample = check string `{
  "text" : "Leave at the door"
}`.fromJsonString();

@test:Config {groups: ["api-spec"]}
function testGetOrdersAnswersNotImplemented() returns error? {
    http:Response response = check ordersConfigClient->get("/api/orders");
    test:assertEquals(response.statusCode, 501);
}

@test:Config {groups: ["api-spec"]}
function testPostOrdersRequestExample() returns error? {
    Order _ = check constraint:validate(postOrdersRequestExample);
}

@test:Config {groups: ["api-spec"]}
function testPostOrders201ResponseExample() returns error? {
    Order _ = check constraint:validate(check string `{
      "orderId" : "1001",
      "status" : "NEW",
      "quantity" : 1,
      "addresses" : [ {
        "street" : "Main St"
      } ]
    }`.fromJsonString());
}

@test:Config {groups: ["api-spec"]}
function testGetOrdersOrderIdRejectsMissingRequiredHeaders() returns error? {
    http:Response response = check ordersConfigClient->get("/api/orders/test");
    test:assertEquals(response.statusCode, 400);
}

@test:Config {groups: ["api-spec"]}
function testPostOrdersOrderIdNotesRequestExample() returns error? {
    Note _ = check constraint:validate(postOrdersOrderIdNotesRequestExample);
}

@test:Config {groups: ["api-spec"]}
function testPostOrdersOrderIdNotesRejectsMissingRequiredHeaders() returns error? {
    http:Response response = check ordersConfigClient->post("/api/orders/test/notes", postOrdersOrderIdNotesRequestExample);
    test:assertEquals(response.statusCode, 400);
}

@test:Config {groups: ["api-spec"]}
function testOrdersConfigAnswersNotFoundToUnknownPath() returns error? {
    http:Response response = check ordersConfigClient->get("/api/api-spec-test/no/such/path");
    test:assertEquals(response.statusCode, 404);
}
