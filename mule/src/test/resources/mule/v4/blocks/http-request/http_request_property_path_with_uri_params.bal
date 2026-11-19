import ballerina/http;
import ballerina/lang.regexp;
import ballerina/url;
import ballerina/uuid;

public type Vars record {|
    string orderId?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
    string correlationId = uuid:createType4AsString();
|};

configurable string backend_orders_path = "/orders/{id}";

public function demoFlow(Context ctx) {
    ctx.vars.orderId = "1001";

    // http client request
    http:Client backend = check new ("https://orders.example.com:80");
    map<string?> uriParams_nilable = {
        "id": ctx.vars?.orderId.toString()
    };
    map<string> uriParams = map from string key in uriParams_nilable.keys()
        where uriParams_nilable.get(key) is string
        select [key, <string>uriParams_nilable.get(key)];
    string queryPath = check pathBuilder0(string `${backend_orders_path}`, uriParams, {});
    http:Response clientResult0 = check backend->get(queryPath, headers = {"X-Correlation-ID": ctx.correlationId});
    ctx.payload = check clientResult0.getJsonPayload();
}

function pathBuilder0(string path, map<string> uriParams, map<string> queryParams) returns string|error {
    // TODO: Instead try to use clientResult0->backend./basePath/[id].get
    string requestPath = path;
    foreach var [key, value] in uriParams.entries() {
        requestPath = regexp:replaceAll(check regexp:fromString(string `\{${key}\}`), requestPath, value);
    }
    string[] queryParts = [];
    foreach var [key, value] in queryParams.entries() {
        queryParts.push(string `${check url:encode(key, "UTF-8")}=${check url:encode(value, "UTF-8")}`);
    }
    if queryParts.length() > 0 {
        requestPath += (requestPath.includes("?") ? "&" : "?") + string:'join("&", ...queryParts);
    }
    return requestPath;
}
