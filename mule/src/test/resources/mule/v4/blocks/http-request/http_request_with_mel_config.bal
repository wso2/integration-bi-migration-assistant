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
    map<string?> headers_nilable = {
        "client-id": anypoint_auth_client_id,
        "client-secret": anypoint_auth_client_secret,
        "Content-Type": "application/json"
    };
    map<string> headers = map from string key in headers_nilable.keys()
        where headers_nilable.get(key) is string
        select [key, <string>headers_nilable.get(key)];
    map<string?> uriParams_nilable = {
        "id": ctx.vars?.id.toString()
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
