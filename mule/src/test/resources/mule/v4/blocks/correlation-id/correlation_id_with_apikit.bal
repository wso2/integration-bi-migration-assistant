import ballerina/http;
import ballerina/log;
import ballerina/uuid;

public type Vars record {|
    anydata outboundHeaders?;
    anydata httpStatus?;
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

public type APIKIT__NOT_FOUND distinct error;

public listener http:Listener listener\-config = new (8081);

service http:InterceptableService / on listener\-config {
    function init() returns error? {
    }

    public function createInterceptors() returns [MuleRequestInterceptor0, MuleResponseErrorInterceptor0, MuleResponseInterceptor0] {
        return [new MuleRequestInterceptor0(), new MuleResponseErrorInterceptor0(), new MuleResponseInterceptor0()];
    }

    resource function default api/[string... path](http:Request request, http:RequestContext requestContext) returns http:Response|error {
        _ = getSharedCorrelationId(requestContext, request);
        return error APIKIT__NOT_FOUND("APIKIT:NOT_FOUND");
    }

    resource function get api/orders(http:Request request, http:RequestContext requestContext) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getSharedCorrelationId(requestContext, request)};

        // set payload
        anydata payload1 = {orders: [], correlationId: ctx.correlationId};
        ctx.payload = payload1;

        http:Response response = <http:Response>ctx.attributes.response;
        response.setPayload(ctx.payload);

        // http response headers
        anydata responseHeaderValues = ctx.vars?.outboundHeaders ?: {};
        map<string> responseHeaders = check responseHeaderValues.cloneWithType();
        foreach [string, string] [headerName, headerValue] in responseHeaders.entries() {
            response.setHeader(headerName, headerValue);
        }

        // http response status code
        response.statusCode = check int:fromString((ctx.vars?.httpStatus ?: 200).toString());
        return response;
    }
}

service class MuleRequestInterceptor0 {
    *http:RequestInterceptor;

    resource function 'default [string... path](http:RequestContext requestContext, http:Request request) returns http:NextService|error? {
        Context ctx = {attributes: {request: request, response: new}, correlationId: getSharedCorrelationId(requestContext, request)};
        log:printInfo("Request " + ctx.correlationId.toString());
        return requestContext.next();
    }
}

service class MuleResponseErrorInterceptor0 {
    *http:ResponseErrorInterceptor;

    remote function interceptResponseError(http:RequestContext requestContext, http:Response interceptedResponse, error err) returns http:Response|error {
        Context ctx = {attributes: {response: interceptedResponse}, correlationId: getSharedCorrelationId(requestContext)};
        // on-error-propagate
        ctx.vars.httpStatus = "404";

        // set payload
        anydata payload0 = {"message": "Resource not found", correlationId: ctx.correlationId};
        ctx.payload = payload0;
        http:Response response = <http:Response>ctx.attributes.response;
        response.statusCode = 500;
        if ctx.payload !is () {
            (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        }

        // http response headers
        anydata responseHeaderValues = ctx.vars?.outboundHeaders ?: {};
        map<string> responseHeaders = check responseHeaderValues.cloneWithType();
        foreach [string, string] [headerName, headerValue] in responseHeaders.entries() {
            interceptedResponse.setHeader(headerName, headerValue);
        }

        // http response status code
        interceptedResponse.statusCode = check int:fromString((ctx.vars?.httpStatus ?: 500).toString());
        return <http:Response>ctx.attributes.response;
    }
}

service class MuleResponseInterceptor0 {
    *http:ResponseInterceptor;

    remote function interceptResponse(http:RequestContext requestContext, http:Response response) returns http:Response|error {
        Context ctx = {attributes: {response: response}, correlationId: getSharedCorrelationId(requestContext)};
        log:printInfo("Response " + ctx.correlationId.toString());
        if ctx.payload !is () {
            (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        }
        return response;
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

function getSharedCorrelationId(http:RequestContext requestContext, http:Request? request = ()) returns string {
    // Mule keeps one ID for a whole request, so the resource and the interceptors use the ID the first of them made
    string|error id = requestContext.getWithType("correlationId");
    if id is string {
        return id;
    }
    string newId = request is () ? uuid:createType4AsString() : getCorrelationId(request);
    requestContext.set("correlationId", newId);
    return newId;
}
