import ballerina/http;

public type Vars record {|
    string orderId?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function demoFlow(Context ctx) {
    ctx.vars.orderId = "1001";

    // http client request
    http:Client http_request_config = check new ("https://orders.example.com:80");
    map<string?> uriParams_nilable = {
        "id": ctx.vars?.orderId.toString()
    };
    map<string> uriParams = map from string key in uriParams_nilable.keys()
        where uriParams_nilable.get(key) is string
        select [key, <string>uriParams_nilable.get(key)];
    http:Response clientResult0 = check http_request_config->/orders/[uriParams.get("id")]/items.get();
    ctx.payload = check clientResult0.getJsonPayload();
}
