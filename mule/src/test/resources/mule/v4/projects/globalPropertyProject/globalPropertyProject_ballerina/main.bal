import ballerina/http;

public listener http:Listener HTTP_Listener_config = new (8081);

service / on HTTP_Listener_config {
    function init() returns error? {
    }

    resource function default orders(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};

        // http client request
        http:Client HTTP_Request_configuration_order = check new ("localhost:9090");
        map<string?> headers_nilable = {
            "client-id": anypoint_auth_client_id
        };
        map<string> headers = map from string key in headers_nilable.keys()
            where headers_nilable.get(key) is string
            select [key, <string>headers_nilable.get(key)];
        if headers.keys().every(key => !key.equalsIgnoreCaseAscii("X-Correlation-ID")) {
            headers["X-Correlation-ID"] = ctx.correlationId;
        }
        http:Response clientResult0 = check HTTP_Request_configuration_order->/orders.get(headers);
        ctx.payload = check clientResult0.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
