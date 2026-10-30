import ballerina/http;
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

public listener http:Listener listener_config = new (8081);

service /mule4 on listener_config {
    function init() returns error? {
    }

    resource function get orders/[string id](http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {id}}, correlationId: getCorrelationId(request)};

        // http client request
        http:Client order\-hybris\-sapi\-http\-request\-configuration = check new ("https://orders.example.com:80");
        map<string?> uriParams_nilable = {
            "id": ctx.attributes.uriParams.get("id").toString()
        };
        map<string> uriParams = map from string key in uriParams_nilable.keys()
            where uriParams_nilable.get(key) is string
            select [key, <string>uriParams_nilable.get(key)];
        map<string?> queryParams_nilable = {
            "language": ctx.attributes.request.getQueryParamValue("language").toString()
        };
        map<string> queryParams = map from string key in queryParams_nilable.keys()
            where queryParams_nilable.get(key) is string
            select [key, <string>queryParams_nilable.get(key)];
        http:Response clientResult0 = check order\-hybris\-sapi\-http\-request\-configuration->/orders/[uriParams.get("id")].get(headers = {"X-Correlation-ID": ctx.correlationId}, params = check queryParams.cloneWithType());
        ctx.payload = check clientResult0.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

function getCorrelationId(http:Request request) returns string {
    // Mule takes the caller's X-Correlation-ID, then its MULE_CORRELATION_ID, and makes an ID when neither is sent
    string|http:HeaderNotFoundError id = request.getHeader("X-Correlation-ID");
    if id !is string {
        id = request.getHeader("MULE_CORRELATION_ID");
    }
    return id is string ? id : uuid:createType4AsString();
}
