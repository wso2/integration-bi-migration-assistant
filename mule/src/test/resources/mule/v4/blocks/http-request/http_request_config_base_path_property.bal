import ballerina/http;

public type Context record {|
    anydata payload = ();
|};

configurable string api_basePath = "/api/v1";

public function demoFlow(Context ctx) {

    // http client request
    http:Client http_request_config = check new (string `https://orders.example.com:80${api_basePath}`);
    http:Response clientResult0 = check http_request_config->/orders.get();
    ctx.payload = check clientResult0.getJsonPayload();
}
