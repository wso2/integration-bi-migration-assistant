import ballerina/http;

public type Vars record {|
    int timeout?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

configurable string http_timeout = "10000";

public function propertyTimeoutFlow(Context ctx) {

    // http client request

    // TODO: failed to convert responseTimeout ${http.timeout}
    http:Client http_request_config = check new ("https://orders.example.com:80");
    http:Response clientResult0 = check http_request_config->/orders.get();
    ctx.payload = check clientResult0.getJsonPayload();
}

public function expressionTimeoutFlow(Context ctx) {
    ctx.vars.timeout = 5000;

    // http client request

    // TODO: failed to convert responseTimeout #[vars.timeout]
    http:Client http_request_config = check new ("https://orders.example.com:80");
    http:Response clientResult1 = check http_request_config->/orders.get();
    ctx.payload = check clientResult1.getJsonPayload();
}
