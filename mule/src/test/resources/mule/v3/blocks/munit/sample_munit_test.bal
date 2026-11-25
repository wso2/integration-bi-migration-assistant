import ballerina/log;
import ballerina/test;

@test:BeforeSuite
function beforeSuiteSetup() returns error? {
    log:printInfo("Setting up test suite");
}

@test:AfterSuite
function afterSuiteCleanup() returns error? {
    log:printInfo("Cleaning up test suite");
}

@test:Config {}
function testAssertOnEquals() returns error? {
    // Test: Test with assert-on-equals
    // Execution (Act)
    // set payload
    anydata payload0 = {"status": "OK"};
    ctx.payload = payload0;
    // Validation (Assert)
    test:assertEquals(ctx.payload.status, "OK");
}

@test:Config {}
function testWithMock() returns error? {
    // Test: Test with munit mock
    // Behavior (Setup)
    // TODO: MUNIT MOCK — MANUAL CONVERSION REQUIRED.
    // ------------------------------------------------------------------------
    // <munit:mock messageProcessor="http:request" doc:name="Mock HTTP Request">
    //     <munit:with-attributes>
    //         <munit:with-attribute name="config-ref" whereValue="HTTP_Request_Config"/>
    //     </munit:with-attributes>
    //     <munit:when>
    //         <munit:with-payload payload='#[{"orderId": 123}]' mimeType="application/json"/>
    //     </munit:when>
    // </munit:mock>
    // ------------------------------------------------------------------------
    // Execution (Act)
    // TODO: failed to resolve flow getOrderFlow
    check getOrderFlow(ctx);
    // Validation (Assert)
    test:assertNotEquals(ctx.payload, ());
    test:assertEquals(ctx.payload.orderId, 123);
}

@test:Config {}
function testFail() returns error? {
    // Test: Test with explicit fail
    // Validation (Assert)
    test:assertFail("This test should not pass");
}
