import ballerina/http;
import ballerina/log;

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
|};

public type APIKIT__NOT_FOUND distinct error;

public type APIKIT__BAD_REQUEST distinct error;

public type APIKIT__METHOD_NOT_ALLOWED distinct error;

public type APIKIT__NOT_ACCEPTABLE distinct error;

public type APIKIT__UNSUPPORTED_MEDIA_TYPE distinct error;

public type APIKIT__NOT_IMPLEMENTED distinct error;

public listener http:Listener listener\-config = new (8081);

service http:InterceptableService / on listener\-config {
    function init() returns error? {
    }

    public function createInterceptors() returns [MuleResponseErrorInterceptor0] {
        return [new MuleResponseErrorInterceptor0()];
    }

    resource function default [string... path](http:Request request) returns http:Response|error {
        return error APIKIT__NOT_FOUND("APIKIT:NOT_FOUND");
    }

    resource function get orders/[string id](http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {id}}};
        log:printInfo("Get order");

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

    resource function post orders(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        log:printInfo("Create order");

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

            // on-error-propagate

            ctx.vars.httpStatus = "400";

            // set payload

            string payload0 = "{\"message\":\"Bad request\"}";
            ctx.payload = payload0;
            http:Response response = <http:Response>ctx.attributes.response;
            response.statusCode = 500;
        } else if err is APIKIT__NOT_FOUND|http:ResourceNotFoundError {
            // on-error-continue
            ctx.vars.httpStatus = "404";

            // set payload
            string payload1 = "{\"message\":\"Resource not found\"}";
            ctx.payload = payload1;
        } else if err is APIKIT__METHOD_NOT_ALLOWED|http:ResourceMethodNotAllowedError {
            // on-error-propagate
            ctx.vars.httpStatus = "405";

            // set payload
            string payload2 = "{\"message\":\"Method not allowed\"}";
            ctx.payload = payload2;
            http:Response response = <http:Response>ctx.attributes.response;
            response.statusCode = 500;
        } else if err is APIKIT__NOT_ACCEPTABLE|http:RequestNotAcceptableError {
            // on-error-propagate
            ctx.vars.httpStatus = "406";

            // set payload
            string payload3 = "{\"message\":\"Not acceptable\"}";
            ctx.payload = payload3;
            http:Response response = <http:Response>ctx.attributes.response;
            response.statusCode = 500;
        } else if err is APIKIT__UNSUPPORTED_MEDIA_TYPE|http:UnsupportedRequestMediaTypeError {
            // on-error-propagate
            ctx.vars.httpStatus = "415";

            // set payload
            string payload4 = "{\"message\":\"Unsupported media type\"}";
            ctx.payload = payload4;
            http:Response response = <http:Response>ctx.attributes.response;
            response.statusCode = 500;
        } else if err is APIKIT__NOT_IMPLEMENTED {
            // on-error-propagate
            ctx.vars.httpStatus = "501";

            // set payload
            string payload5 = "{\"message\":\"Not implemented\"}";
            ctx.payload = payload5;
            http:Response response = <http:Response>ctx.attributes.response;
            response.statusCode = 500;
        }
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
