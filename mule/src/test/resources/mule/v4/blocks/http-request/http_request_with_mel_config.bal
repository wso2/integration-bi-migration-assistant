import ballerina/http;
import ballerina/lang.regexp;
import ballerina/log;

public type Context record {|
    anydata payload = ();
|};

configurable string secure_api_endpoint = ?;
configurable string secure_api_host = ?;
configurable string secure_api_port = ?;
configurable string anypoint_auth_client_id = ?;
configurable string anypoint_auth_client_secret = ?;

function pathBuilder0(string path, map<string> uriParams, map<string> queryParams) returns string { // Use a path builder
    string requestPath = path;
    foreach var [key, value] in uriParams.entries() {
        requestPath = regexp:replaceAll(check regexp:fromString(string `\{${key}\}`), requestPath, value);
    }
    foreach var [key, value] in queryParams.entries() {
        requestPath = regexp:replaceAll(check regexp:fromString(string `\{${key}\}`), requestPath, value);
    }
    return requestPath;
}

public function demoFlow(Context ctx) {

    // http client request
    http:Client http_request_config = check new (string `${secure_api_host}:${secure_api_port}`);
    map<string> _headers_ = {
        "client-id": anypoint_auth_client_id,
        "client-secret": anypoint_auth_client_secret,
        "Content-Type": "application/json"
    };
    map<string> _uri_params_ = {
        "id": ctx.vars.id
    };
    map<string> _query_params_ = {
        "language": ctx.vars.language
    };
    string queryPath = pathBuilder0(string `${secure_api_endpoint}`, _uri_params_, _query_params_);
    http:Response clientResult0 = check http_request_config->get(queryPath, _headers_);
    ctx.payload = check clientResult0.getJsonPayload();
    log:printInfo(string `Received from external API: ${ctx.payload.toString()}`);
}
