import ballerina/http;
import ballerina/log;

public type Context record {|
    anydata payload = ();
|};

configurable string anypoint_auth_client_id = ?;
configurable string anypoint_auth_client_secret = ?;

public function demoFlow(Context ctx) {

    // http client request
    http:Client http_request_config = check new ("jsonplaceholder.typicode.com:80");
    map<string> _headers_ = {
        "client-id": anypoint_auth_client_id,
        "client-secret": anypoint_auth_client_secret,
        "Content-Type": "application/json",
            };
    map<string> _uri_params_ = {
        "id": ctx.vars.id
    };
    map<string> _query_params_ = {
        "language": ctx.vars.language
    };
    http:Response clientResult0 = check http_request_config->/posts/latest.get(_headers_, _uri_params_, _query_params_);
    ctx.payload = check clientResult0.getJsonPayload();
    log:printInfo(string `Received from external API: ${ctx.payload.toString()}`);
}
