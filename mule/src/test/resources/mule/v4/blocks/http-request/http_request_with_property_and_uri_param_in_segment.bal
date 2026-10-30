import ballerina/http;
import ballerina/uuid;

public type Vars record {|
    string orderId?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
    string correlationId = uuid:createType4AsString();
|};

configurable string api_region = "eu";

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
    http:Response clientResult0 = check http_request_config->/orders/[string `${api_region}-${uriParams.get("id")}`].get(headers = {"X-Correlation-ID": ctx.correlationId});
    ctx.payload = check clientResult0.getJsonPayload();
}
