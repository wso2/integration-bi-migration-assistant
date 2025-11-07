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

public function demoFlow(Context ctx) {

    // http client request
    http:Client http_request_config = check new (string `${secure_api_host}:${secure_api_port}`);
    map<string> headers = {
        "client-id": anypoint_auth_client_id,
        "client-secret": anypoint_auth_client_secret,
        "Content-Type": "application/json"
    };
    map<string> uriParams = {
        "id": ctx.vars?.id.toString()
    };
    map<string> queryParams = {
        "language": ctx.vars?.language.toString()
    };
    string queryPath = pathBuilder0(string `${secure_api_endpoint}`, uriParams, queryParams);
    http:Response clientResult0 = check http_request_config->get(queryPath, headers);
    ctx.payload = check clientResult0.getJsonPayload();
    log:printInfo(string `Received from external API: ${ctx.payload.toString()}`);
}

function pathBuilder0(string path, map<string> uriParams, map<string> queryParams) returns string {
    // TODO: Instead try to use clientResult0->http_request_config./basePath/[id].get(language=language)
    string requestPath = path;
    foreach var [key, value] in uriParams.entries() {
        requestPath = regexp:replaceAll(check regexp:fromString(string `\{${key}\}`), requestPath, value);
    }
    foreach var [key, value] in queryParams.entries() {
        requestPath = regexp:replaceAll(check regexp:fromString(string `\{${key}\}`), requestPath, value);
    }
    return requestPath;
}
