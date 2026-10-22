import ballerina/http;
import ballerina/uuid;

public type Context record {|
    anydata payload = ();
    string correlationId = uuid:createType4AsString();
|};

public function demoFlow(Context ctx) {

    // http client request
    http:Client http_request_config = check new ("https://orders.example.com:80", timeout = 10);
    http:Response clientResult0 = check http_request_config->/orders.get(headers = {"X-Correlation-ID": ctx.correlationId});
    ctx.payload = check clientResult0.getJsonPayload();
}

// TODO: UNSUPPORTED MULE BLOCK ENCOUNTERED. MANUAL CONVERSION REQUIRED.
// ------------------------------------------------------------------------
// <configuration defaultResponseTimeout="10000" shutdownTimeout="5000" xmlns="http://www.mulesoft.org/schema/mule/core"/>
// ------------------------------------------------------------------------

