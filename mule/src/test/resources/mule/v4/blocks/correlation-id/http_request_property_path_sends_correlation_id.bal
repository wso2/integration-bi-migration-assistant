import ballerina/http;
import ballerina/lang.regexp;
import ballerina/uuid;

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
    string correlationId = uuid:createType4AsString();
|};

configurable string orders_path = "/orders";
public listener http:Listener listener_config = new (8081);

service / on listener_config {
    function init() returns error? {
    }

    resource function default property\-path(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};

        // http client request
        http:Client request_config = check new ("localhost:8082");
        string queryPath = pathBuilder0(string `${orders_path}`, {}, {});
        http:Response clientResult0 = check request_config->get(queryPath, headers = {"X-Correlation-ID": ctx.correlationId});
        ctx.payload = check clientResult0.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }

    resource function default property\-path\-with\-headers(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};

        // http client request
        http:Client request_config = check new ("localhost:8082");
        map<string?> headers_nilable = {"accept": "application/json"};
        map<string> headers = map from string key in headers_nilable.keys()
            where headers_nilable.get(key) is string
            select [key, <string>headers_nilable.get(key)];
        if headers.keys().every(key => !key.equalsIgnoreCaseAscii("X-Correlation-ID")) {
            headers["X-Correlation-ID"] = ctx.correlationId;
        }
        string queryPath = pathBuilder1(string `${orders_path}`, {}, {});
        http:Response clientResult1 = check request_config->get(queryPath, headers);
        ctx.payload = check clientResult1.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

function pathBuilder0(string path, map<string> uriParams, map<string> queryParams) returns string {
    // TODO: Instead try to use clientResult0->request_config./basePath/.get
    string requestPath = path;
    foreach var [key, value] in uriParams.entries() {
        requestPath = regexp:replaceAll(check regexp:fromString(string `\{${key}\}`), requestPath, value);
    }
    foreach var [key, value] in queryParams.entries() {
        requestPath = regexp:replaceAll(check regexp:fromString(string `\{${key}\}`), requestPath, value);
    }
    return requestPath;
}

function pathBuilder1(string path, map<string> uriParams, map<string> queryParams) returns string {
    // TODO: Instead try to use clientResult1->request_config./basePath/.get
    string requestPath = path;
    foreach var [key, value] in uriParams.entries() {
        requestPath = regexp:replaceAll(check regexp:fromString(string `\{${key}\}`), requestPath, value);
    }
    foreach var [key, value] in queryParams.entries() {
        requestPath = regexp:replaceAll(check regexp:fromString(string `\{${key}\}`), requestPath, value);
    }
    return requestPath;
}

function getCorrelationId(http:Request request) returns string {
    // Mule takes the caller's X-Correlation-ID, then its MULE_CORRELATION_ID, and makes an ID when neither is sent
    string|http:HeaderNotFoundError id = request.getHeader("X-Correlation-ID");
    if id !is string {
        id = request.getHeader("MULE_CORRELATION_ID");
    }
    return id is string ? id : uuid:createType4AsString();
}
