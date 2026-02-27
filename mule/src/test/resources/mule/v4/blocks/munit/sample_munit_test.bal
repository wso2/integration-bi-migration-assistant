import ballerina/test;

@test:BeforeSuite
function before\-suite\-setup() returns error? {
    log:printInfo("Setting up test suite");
}

@test:AfterSuite
function after\-suite\-cleanup() returns error? {
    log:printInfo("Cleaning up test suite");
}

@test:Config {}
function test\-simple\-assert\-equals() returns error? {
    // Test: Test with simple assert equals
    // Execution (Act)
    // set payload
    anydata payload0 = {"status": "OK"};
    ctx.payload = payload0;
    // Validation (Assert)
    test:assertEquals(ctx.payload.status, 'OK ');
}

@test:Config {}
function test\-with\-mock() returns error? {
    // Test: Test with mock-when
    // Behavior (Setup)
    // TODO: MUNIT MOCK — MANUAL CONVERSION REQUIRED.
    // ------------------------------------------------------------------------
    // <munit-tools:mock-when processor="http:request" doc:name="Mock HTTP Request">
    //     <munit-tools:with-attributes>
    //         <munit-tools:with-attribute attributeName="config-ref" whereValue="HTTP_Request_Config"/>
    //     </munit-tools:with-attributes>
    //     <munit-tools:then-return>
    //         <munit-tools:payload value='#[{"orderId": 123}]' mediaType="application/json"/>
    //     </munit-tools:then-return>
    // </munit-tools:mock-when>
    // ------------------------------------------------------------------------
    // Execution (Act)
    // FIXME: failed to find flow getOrderFlow
    getOrderFlow(ctx);
    // Validation (Assert)
    test:assertNotEquals(ctx.payload, ());
    test:assertEquals(ctx.payload.orderId, 123);
}

@test:Config {}
function test\-assert\-fail() returns error? {
    // Test: Test with explicit fail
    // Validation (Assert)
    test:assertFail("This test should not pass");
}
