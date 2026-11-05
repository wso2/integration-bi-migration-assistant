import ballerina/http;
import ballerina/log;
import ballerina/uuid;

public type Context record {|
    anydata payload = ();
    string correlationId = uuid:createType4AsString();
|};

configurable string anypoint_auth_client_id = ?;
configurable string anypoint_auth_client_secret = ?;

public function demoFlow(Context ctx) {

    // http client request
    http:Client http_request_config = check new ("jsonplaceholder.typicode.com:80");
    map<string?> headers_nilable = {
        "client-id": anypoint_auth_client_id,
        "client-secret": anypoint_auth_client_secret,
        "Content-Type": "application/json"
    };
    map<string> headers = map from string key in headers_nilable.keys()
        where headers_nilable.get(key) is string
        select [key, <string>headers_nilable.get(key)];
    if headers.keys().every(key => !key.equalsIgnoreCaseAscii("X-Correlation-ID")) {
        headers["X-Correlation-ID"] = ctx.correlationId;
    }
    http:Response clientResult0 = check http_request_config->/posts/latest.get(headers);
    ctx.payload = check clientResult0.getJsonPayload();
    log:printInfo(string `Received from external API: ${ctx.payload.toString()}`);
}
