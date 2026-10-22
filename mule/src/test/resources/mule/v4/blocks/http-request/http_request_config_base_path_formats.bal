import ballerina/http;
import ballerina/uuid;

public type Context record {|
    anydata payload = ();
    string correlationId = uuid:createType4AsString();
|};

public function apiFlow(Context ctx) {

    // http client request
    http:Client api_request_config = check new ("https://orders.example.com:80/api/v1");
    http:Response clientResult0 = check api_request_config->/orders.get(headers = {"X-Correlation-ID": ctx.correlationId});
    ctx.payload = check clientResult0.getJsonPayload();
}

public function rootFlow(Context ctx) {

    // http client request
    http:Client root_request_config = check new ("https://orders.example.com:80");
    http:Response clientResult1 = check root_request_config->/orders.get(headers = {"X-Correlation-ID": ctx.correlationId});
    ctx.payload = check clientResult1.getJsonPayload();
}
