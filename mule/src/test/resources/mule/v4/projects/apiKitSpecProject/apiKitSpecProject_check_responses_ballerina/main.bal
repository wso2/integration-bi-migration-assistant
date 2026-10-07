import ballerina/constraint;
import ballerina/http;
import ballerina/log;

public listener http:Listener http\-listener\-config = new (8080);

// TODO: In Mule, API Manager enforced policies on this API, and this service does not enforce them:
//   - API 18291872 in API Manager (api-gateway:autodiscovery); check its policies there
//   - client-id-enforcement trait of the API spec, on 1 of 5 operations
//   - basic security scheme of the API spec, on 5 of 5 operations
// Put an API gateway in front of this service, or add the checks, for example in an http:RequestInterceptor.
service http:InterceptableService / on http\-listener\-config {
    function init() returns error? {
    }

    public function createInterceptors() returns [MuleResponseErrorInterceptor0, MuleResponseInterceptor0] {
        return [new MuleResponseErrorInterceptor0(), new MuleResponseInterceptor0()];
    }

    resource function default api/[string... path](http:Request request) returns http:Response|error {
        return error APIKIT__NOT_FOUND("APIKIT:NOT_FOUND");
    }

    resource function get api/orders(string? status, http:Request request) returns error {
        // No flow implements this operation of the API spec
        return error APIKIT__NOT_IMPLEMENTED("APIKIT:NOT_IMPLEMENTED");
    }

    resource function get api/orders/[string orderId](boolean? expand, @http:Header MarketId marketId, @http:Header {name: "x-correlation-id"} string? xCorrelationId, http:Request request) returns PriorityOrderOk|http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {orderId}}};
        log:printInfo(string `Fetching order ${ctx.attributes.uriParams.get("orderId").toString()}`);

        // set payload
        string payload4 = "{\"orderId\": \"1\", \"status\": \"NEW\", \"quantity\": 2, \"addresses\": [{\"street\": \"Main\"}], \"priority\": true}";
        ctx.payload = payload4;

        // http response headers
        anydata responseHeaderValues = ctx.vars?.outboundHeaders ?: {};
        map<string> responseHeaders = check responseHeaderValues.cloneWithType();

        // http response status code
        int statusCode = check int:fromString((ctx.vars?.httpStatus ?: 200).toString());

        // The payload is checked against the body the API spec declares for the status; other statuses send it unchecked
        if statusCode == 200 {
            return <PriorityOrderOk>{headers: responseHeaders, body: check constraint:validate(check jsonPayload(ctx.payload))};
        }
        http:Response response = <http:Response>ctx.attributes.response;
        response.setPayload(ctx.payload);
        foreach [string, string] [headerName, headerValue] in responseHeaders.entries() {
            response.setHeader(headerName, headerValue);
        }
        response.statusCode = statusCode;
        return response;
    }

    resource function post api/orders(@http:Payload Order payload, http:Request request) returns OrderCreated|http:Response|error {
        Context ctx = {payload, attributes: {request, response: new}};
        log:printInfo(string `Creating order ${ctx.payload.toString()}`);
        ctx.vars.httpStatus = "201";

        // http response headers
        anydata responseHeaderValues = ctx.vars?.outboundHeaders ?: {};
        map<string> responseHeaders = check responseHeaderValues.cloneWithType();

        // http response status code
        int statusCode = check int:fromString((ctx.vars?.httpStatus ?: 200).toString());

        // The payload is checked against the body the API spec declares for the status; other statuses send it unchecked
        if statusCode == 201 {
            return <OrderCreated>{headers: responseHeaders, body: check constraint:validate(check jsonPayload(ctx.payload))};
        }
        http:Response response = <http:Response>ctx.attributes.response;
        response.setPayload(ctx.payload);
        foreach [string, string] [headerName, headerValue] in responseHeaders.entries() {
            response.setHeader(headerName, headerValue);
        }
        response.statusCode = statusCode;
        return response;
    }

    resource function put api/orders/[string orderId](@http:Payload PriorityOrder payload, http:Request request) returns PriorityOrderOk|http:Response|error {
        Context ctx = {payload, attributes: {request, response: new, uriParams: {orderId}}};
        log:printInfo(string `Replacing order ${ctx.attributes.uriParams.get("orderId").toString()}`);
        ctx.vars.httpStatus = "202";

        // http response headers
        anydata responseHeaderValues = ctx.vars?.outboundHeaders ?: {};
        map<string> responseHeaders = check responseHeaderValues.cloneWithType();

        // http response status code
        int statusCode = check int:fromString((ctx.vars?.httpStatus ?: 200).toString());

        // The payload is checked against the body the API spec declares for the status; other statuses send it unchecked
        if statusCode == 200 {
            return <PriorityOrderOk>{headers: responseHeaders, body: check constraint:validate(check jsonPayload(ctx.payload))};
        }
        http:Response response = <http:Response>ctx.attributes.response;
        response.setPayload(ctx.payload);
        foreach [string, string] [headerName, headerValue] in responseHeaders.entries() {
            response.setHeader(headerName, headerValue);
        }
        response.statusCode = statusCode;
        return response;
    }

    resource function post api/orders/[string orderId]/notes(@http:Header {name: "client_id"} string clientId, @http:Header {name: "client_secret"} string clientSecret, @http:Payload Note payload, http:Request request) returns http:Created|http:Response|error {
        Context ctx = {payload, attributes: {request, response: new, uriParams: {orderId}}};
        log:printInfo(string `Adding note ${ctx.payload.toString()}`);
        ctx.vars.httpStatus = "201";

        // http response headers
        anydata responseHeaderValues = ctx.vars?.outboundHeaders ?: {};
        map<string> responseHeaders = check responseHeaderValues.cloneWithType();

        // http response status code
        int statusCode = check int:fromString((ctx.vars?.httpStatus ?: 200).toString());

        // The payload is checked against the body the API spec declares for the status; other statuses send it unchecked
        if statusCode == 201 {
            return <http:Created>{headers: responseHeaders, body: check jsonPayload(ctx.payload)};
        }
        http:Response response = <http:Response>ctx.attributes.response;
        response.setPayload(ctx.payload);
        foreach [string, string] [headerName, headerValue] in responseHeaders.entries() {
            response.setHeader(headerName, headerValue);
        }
        response.statusCode = statusCode;
        return response;
    }

    resource function delete api/orders/[string orderId](http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {orderId}}};
        log:printInfo(string `Deleting order ${ctx.attributes.uriParams.get("orderId").toString()}`);

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

service class MuleResponseErrorInterceptor0 {
    *http:ResponseErrorInterceptor;

    remote function interceptResponseError(http:RequestContext requestContext, http:Response interceptedResponse, error err) returns http:Response|error {
        Context ctx = {attributes: {response: interceptedResponse}};
        // TODO: if conditions may require some manual adjustments
        if err is APIKIT__BAD_REQUEST|http:HeaderBindingError|http:QueryParameterBindingError|http:PathParameterBindingError|http:PayloadBindingError|http:HeaderValidationError|http:QueryParameterValidationError|http:PayloadValidationError {

            // on-error-continue

            // set payload

            string payload0 = "{\"message\": \"Bad request\"}";
            ctx.payload = payload0;
            ctx.vars.httpStatus = "400";
        } else if err is APIKIT__NOT_FOUND|http:ResourceNotFoundError {
            // on-error-continue

            // set payload
            string payload1 = "{\"message\": \"Resource not found\"}";
            ctx.payload = payload1;
            ctx.vars.httpStatus = "404";
        } else if err is APIKIT__NOT_IMPLEMENTED {
            // on-error-continue

            // set payload
            string payload2 = "{\"message\": \"Not Implemented\"}";
            ctx.payload = payload2;
            ctx.vars.httpStatus = "501";
        }

        // set payload
        anydata payload3 = ctx.payload;
        ctx.payload = payload3;
        if ctx.payload !is () {
            (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        }

        // http response status code
        interceptedResponse.statusCode = check int:fromString((ctx.vars?.httpStatus ?: 500).toString());
        return <http:Response>ctx.attributes.response;
    }
}

service class MuleResponseInterceptor0 {
    *http:ResponseInterceptor;

    remote function interceptResponse(http:RequestContext requestContext, http:Response response) returns http:Response|error {
        Context ctx = {attributes: {response: response}};
        log:printInfo("After APIkit router");
        if ctx.payload !is () {
            (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        }
        return response;
    }
}
