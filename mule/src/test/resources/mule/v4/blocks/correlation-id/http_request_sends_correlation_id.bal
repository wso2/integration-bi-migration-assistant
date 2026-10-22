import ballerina/http;
import ballerina/uuid;

public type Vars record {|
    string orderId?;
|};

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
    Attributes attributes;
    string correlationId = uuid:createType4AsString();
|};

public listener http:Listener listener_config = new (8081);

service / on listener_config {
    function init() returns error? {
    }

    resource function default auto(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};
        ctx.vars.orderId = "order-1";

        // http client request
        http:Client request_config = check new ("localhost:8082");
        http:Response clientResult0 = check request_config->/auto.get(headers = {"X-Correlation-ID": ctx.correlationId});
        ctx.payload = check clientResult0.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }

    resource function default always(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};
        ctx.vars.orderId = "order-1";

        // http client request
        http:Client request_config = check new ("localhost:8082");
        http:Response clientResult1 = check request_config->/always.get(headers = {"X-Correlation-ID": ctx.correlationId});
        ctx.payload = check clientResult1.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }

    resource function default custom(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};
        ctx.vars.orderId = "order-1";

        // http client request
        http:Client request_config = check new ("localhost:8082");
        http:Response clientResult2 = check request_config->/custom.get(headers = {"X-Correlation-ID": ctx.vars?.orderId.toString()});
        ctx.payload = check clientResult2.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }

    resource function default 'never(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};
        ctx.vars.orderId = "order-1";

        // http client request
        http:Client request_config = check new ("localhost:8082");
        http:Response clientResult3 = check request_config->/'never.get();
        ctx.payload = check clientResult3.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }

    resource function default config\-never(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};
        ctx.vars.orderId = "order-1";

        // http client request
        http:Client never_config = check new ("localhost:8082");
        http:Response clientResult4 = check never_config->/config\-never.get();
        ctx.payload = check clientResult4.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }

    resource function default config\-never\-overridden(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};
        ctx.vars.orderId = "order-1";

        // http client request
        http:Client never_config = check new ("localhost:8082");
        http:Response clientResult5 = check never_config->/config\-never\-overridden.get(headers = {"X-Correlation-ID": ctx.correlationId});
        ctx.payload = check clientResult5.getJsonPayload();

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }

    resource function default explicit(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getCorrelationId(request)};
        ctx.vars.orderId = "order-1";

        // http client request
        http:Client request_config = check new ("localhost:8082");
        map<string?> headers_nilable = {"x-correlation-id": ctx.vars?.orderId.toString(), "accept": "application/json"};
        map<string> headers = map from string key in headers_nilable.keys()
            where headers_nilable.get(key) is string
            select [key, <string>headers_nilable.get(key)];
        if headers.keys().every(key => !key.equalsIgnoreCaseAscii("X-Correlation-ID")) {
            headers["X-Correlation-ID"] = ctx.correlationId;
        }
        http:Response clientResult6 = check request_config->/explicit.get(headers);
        ctx.payload = check clientResult6.getJsonPayload();

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
