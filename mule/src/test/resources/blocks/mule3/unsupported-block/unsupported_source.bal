import ballerina/http;
import ballerina/log;

public type Context record {|
    anydata payload;
|};

public listener http:Listener config = new (8081);

public function demoFlow(Context ctx) {

    // TODO: UNSUPPORTED MULE BLOCK ENCOUNTERED. MANUAL CONVERSION REQUIRED.
    // ------------------------------------------------------------------------
    // <http:listener-unsupported allowedMethods="GET" config-ref="config" xmlns:doc="http://www.mulesoft.org/schema/mule/documentation" doc:name="HTTP" path="/demo" xmlns:http="http://www.mulesoft.org/schema/mule/http"/>
    // ------------------------------------------------------------------------

    log:printInfo("xxx: logger invoked");
}
