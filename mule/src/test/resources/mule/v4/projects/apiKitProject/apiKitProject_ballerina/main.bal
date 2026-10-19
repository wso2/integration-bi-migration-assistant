import ballerina/http;
import ballerina/log;

public listener http:Listener http\-listener\-config = new (8080);

service http:InterceptableService / on http\-listener\-config {
    function init() returns error? {
    }

    public function createInterceptors() returns [MuleRequestInterceptor0, MuleResponseErrorInterceptor0, MuleResponseInterceptor0] {
        return [new MuleRequestInterceptor0(), new MuleResponseErrorInterceptor0(), new MuleResponseInterceptor0()];
    }

    resource function default api(http:Request request) returns http:Response|error {
        return error APIKIT__NOT_FOUND("APIKIT:NOT_FOUND");
    }

    resource function get api/orders(string id, http:Request request) returns error {
        // No flow implements this operation of the API spec
        return error APIKIT__NOT_IMPLEMENTED("APIKIT:NOT_IMPLEMENTED");
    }

    resource function get api/orders/[string id](http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {id}}};
        log:printInfo(string `Received order id: ${id.toString()}`);

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

service class MuleRequestInterceptor0 {
    *http:RequestInterceptor;

    resource function 'default [string... path](http:RequestContext requestContext, http:Request request) returns http:NextService|error? {
        Context ctx = {attributes: {request: request, response: new}};
        log:printInfo("Before APIKit router");
        return requestContext.next();
    }
}

service class MuleResponseErrorInterceptor0 {
    *http:ResponseErrorInterceptor;

    remote function interceptResponseError(http:RequestContext requestContext, http:Response interceptedResponse, error err) returns http:Response|error {
        Context ctx = {attributes: {response: interceptedResponse}};
        // on-error-continue
        log:printError("Message: " + err.message());
        log:printError("Trace: " + err.stackTrace().toString());

        log:printError("APIKit error handled");
        if ctx.payload !is () {
            (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        }
        return <http:Response>ctx.attributes.response;
    }
}

service class MuleResponseInterceptor0 {
    *http:ResponseInterceptor;

    remote function interceptResponse(http:RequestContext requestContext, http:Response response) returns http:Response|error {
        Context ctx = {attributes: {response: response}};
        log:printInfo("After APIKit router");
        if ctx.payload !is () {
            (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        }
        return response;
    }
}
