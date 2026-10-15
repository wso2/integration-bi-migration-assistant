import ballerina/http;

public type Vars record {|
    string customerId?;
    string orderId?;
    string language?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function demoFlow(Context ctx) {
    ctx.vars.customerId = "C100";
    ctx.vars.orderId = "1001";
    ctx.vars.language = "en";
    // http client request
    http:Client http_request_config = check new ("https://orders.example.com:80");
    map<string?> headers_nilable = {
        "Content-Type": "application/json"
    };
    map<string> headers = map from string key in headers_nilable.keys()
        where headers_nilable.get(key) is string
        select [key, <string>headers_nilable.get(key)];
    map<string?> uriParams_nilable = {
        "customerId": ctx.vars?.customerId.toString(),
        "order-id": ctx.vars?.orderId.toString()
    };
    map<string> uriParams = map from string key in uriParams_nilable.keys()
        where uriParams_nilable.get(key) is string
        select [key, <string>uriParams_nilable.get(key)];
    map<string?> queryParams_nilable = {
        "language": ctx.vars?.language.toString()
    };
    map<string> queryParams = map from string key in queryParams_nilable.keys()
        where queryParams_nilable.get(key) is string
        select [key, <string>queryParams_nilable.get(key)];
    http:Response clientResult0 = check http_request_config->/customers/[uriParams.get("customerId")]/orders/[uriParams.get("order-id")].get(headers, params = check queryParams.cloneWithType());
    ctx.payload = check clientResult0.getJsonPayload();
}
