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

service http:InterceptableService / on listener_config {
    function init() returns error? {
    }

    public function createInterceptors() returns [MuleResponseErrorInterceptor0] {
        return [new MuleResponseErrorInterceptor0()];
    }

    resource function default orders(http:Request request, http:RequestContext requestContext) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}, correlationId: getSharedCorrelationId(requestContext, request)};
        do {

            // set payload
            anydata payload0 = 1 / 0;
            ctx.payload = payload0;
        } on fail error err {
            // on-error-propagate

            // set payload
            anydata payload1 = {"message": err.message(), correlationId: ctx.correlationId};
            ctx.payload = payload1;
            http:Response response = <http:Response>ctx.attributes.response;
            response.statusCode = 500;
        }

        http:Response response = <http:Response>ctx.attributes.response;
        response.setPayload(ctx.payload);

        // http response headers
        anydata responseHeaderValues = {"X-Correlation-ID": ctx.correlationId};
        map<string> responseHeaders = check responseHeaderValues.cloneWithType();
        foreach [string, string] [headerName, headerValue] in responseHeaders.entries() {
            response.setHeader(headerName, headerValue);
        }
        return response;
    }
}

service class MuleResponseErrorInterceptor0 {
    *http:ResponseErrorInterceptor;

    remote function interceptResponseError(http:RequestContext requestContext, http:Response interceptedResponse, error err) returns http:Response|error {
        Context ctx = {attributes: {response: interceptedResponse}, correlationId: getSharedCorrelationId(requestContext)};
        if ctx.payload !is () {
            (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        }

        // http response headers
        anydata responseHeaderValues = {"X-Correlation-ID": ctx.correlationId};
        map<string> responseHeaders = check responseHeaderValues.cloneWithType();
        foreach [string, string] [headerName, headerValue] in responseHeaders.entries() {
            interceptedResponse.setHeader(headerName, headerValue);
        }

        // http response status code
        interceptedResponse.statusCode = 500;
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
