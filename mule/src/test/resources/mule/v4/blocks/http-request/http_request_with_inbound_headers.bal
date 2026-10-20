import ballerina/http;

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

public listener http:Listener listener_config = new (8081);

service /mule4 on listener_config {
    function init() returns error? {
    }

    resource function get orders(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};

        // http client request
        http:Client http_request_config = check new ("https://orders.example.com:80");
        map<string?> headers_nilable = {
            "orderType": getOptionalHeader(<http:Request>ctx.attributes.request, "orderType"),
            "lang": getOptionalHeader(<http:Request>ctx.attributes.request, "lang") ?: "en",
            "crmId": getOptionalHeader(<http:Request>ctx.attributes.request, "crmId").toString() != "" ? getOptionalHeader(<http:Request>ctx.attributes.request, "crmId").toString() : (),
            "Content-Type": "application/json"
        };
        map<string> headers = map from string key in headers_nilable.keys()
            where headers_nilable.get(key) is string
            select [key, <string>headers_nilable.get(key)];
        http:Response clientResult0 = check http_request_config->/orders.get(headers);
        ctx.payload = check clientResult0.getJsonPayload();
        // TODO: failed to convert #[attributes.headers.'orderType']

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

function getOptionalHeader(http:Request req, string name) returns string? {
    string|http:HeaderNotFoundError value = req.getHeader(name);
    return value is string ? value : ();
}
