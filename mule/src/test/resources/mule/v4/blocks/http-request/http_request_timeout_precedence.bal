import ballerina/http;
import ballerina/uuid;

public type Context record {|
    anydata payload = ();
    string correlationId = uuid:createType4AsString();
|};

public function configTimeoutFlow(Context ctx) {

    // http client request
    http:Client timeout_request_config = check new ("https://orders.example.com:80", timeout = 5);
    http:Response clientResult0 = check timeout_request_config->/orders.get(headers = {"X-Correlation-ID": ctx.correlationId});
    ctx.payload = check clientResult0.getJsonPayload();
}

public function requestTimeoutFlow(Context ctx) {

    // http client request
    http:Client timeout_request_config = check new ("https://orders.example.com:80", timeout = 2.5);
    http:Response clientResult1 = check timeout_request_config->/orders.get(headers = {"X-Correlation-ID": ctx.correlationId});
    ctx.payload = check clientResult1.getJsonPayload();
}

public function defaultTimeoutFlow(Context ctx) {

    // http client request
    http:Client default_request_config = check new ("https://orders.example.com:80", timeout = 20);
    http:Response clientResult2 = check default_request_config->/orders.get(headers = {"X-Correlation-ID": ctx.correlationId});
    ctx.payload = check clientResult2.getJsonPayload();
}
