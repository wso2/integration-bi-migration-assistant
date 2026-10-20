import ballerina/http;
import ballerina/log;

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
        log:printInfo(string `language: ${(<http:Request>ctx.attributes.request).getQueryParamValue("language") ?: "en"}`);

        // set payload
        string? payload0 = (<http:Request>ctx.attributes.request).getQueryParamValue("language");
        ctx.payload = payload0;

        // http client request
        http:Client http_request_config = check new ("https://orders.example.com:80");
        map<string?> queryParams_nilable = {
            "language": (<http:Request>ctx.attributes.request).getQueryParamValue("language"),
            "currency": (<http:Request>ctx.attributes.request).getQueryParamValue("currency") ?: "USD",
            "channel": (<http:Request>ctx.attributes.request).getQueryParamValue("channel").toString() != "" ? (<http:Request>ctx.attributes.request).getQueryParamValue("channel").toString() : (),
            "keyword": "prefix-" + (<http:Request>ctx.attributes.request).getQueryParamValue("keyword").toString(),
            "source": "web"
        };
        map<string> queryParams = map from string key in queryParams_nilable.keys()
            where queryParams_nilable.get(key) is string
            select [key, <string>queryParams_nilable.get(key)];
        http:Response clientResult0 = check http_request_config->/orders.get(params = check queryParams.cloneWithType());
        ctx.payload = check clientResult0.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
