import ballerina/http;

public type Context record {|
    anydata payload = ();
|};

public function demoFlow(Context ctx) {

    // http client request
    http:Client http_request_config = check new ("https://orders.example.com:80");
    http:Response clientResult0 = check http_request_config->/orders/\{id\}.get();
    ctx.payload = check clientResult0.getJsonPayload();
}
