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

public function demoFlow(Context ctx) {
    ctx.vars.orderId = "1640006670";

    // http client request
    http:Client order\-hybris\-sapi\-http\-request\-configuration = check new ("http://localhost:8084/order-hybris-sapi-v1", timeout = 10);
    map<string?> uriParams_nilable = {
        "id": ctx.vars?.orderId.toString()
    };
    map<string> uriParams = map from string key in uriParams_nilable.keys()
        where uriParams_nilable.get(key) is string
        select [key, <string>uriParams_nilable.get(key)];
    http:Response clientResult0 = check order\-hybris\-sapi\-http\-request\-configuration->/orders/[uriParams.get("id")].get(headers = {"X-Correlation-ID": ctx.correlationId});
    ctx.payload = check clientResult0.getJsonPayload();
}
