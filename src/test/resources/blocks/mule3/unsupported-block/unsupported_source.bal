import ballerina/http;
import ballerina/log;

type Context record {|
    anydata payload;
|};

listener http:Listener config = new (8081, {host: "0.0.0.0"});

function demoFlow(Context ctx) {

    // TODO: UNSUPPORTED MULE BLOCK ENCOUNTERED. MANUAL CONVERSION REQUIRED.
    // ------------------------------------------------------------------------
    // <http:listener-unsupported allowedMethods="GET" config-ref="config" xmlns:doc="http://www.mulesoft.org/schema/mule/documentation" doc:name="HTTP" path="/demo" xmlns:http="http://www.mulesoft.org/schema/mule/http"/>
    // ------------------------------------------------------------------------

    log:printInfo("xxx: logger invoked");
}
