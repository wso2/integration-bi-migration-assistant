import ballerina/http;
import ballerina/uuid;

public type Vars record {|
    string status?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
    string correlationId = uuid:createType4AsString();
|};

public function demoFlow(Context ctx) {
    ctx.vars.status = "OPEN";

    // http client request
    http:Client http_request_config = check new ("https://orders.example.com:80");
    map<string?> queryParams_nilable = {
        "status": ctx.vars?.status.toString(),
        "limit": "10"
    };
    map<string> queryParams = map from string key in queryParams_nilable.keys()
        where queryParams_nilable.get(key) is string
        select [key, <string>queryParams_nilable.get(key)];
    http:Response clientResult0 = check http_request_config->/orders.get(headers = {"X-Correlation-ID": ctx.correlationId}, params = check queryParams.cloneWithType());
    ctx.payload = check clientResult0.getJsonPayload();
}
