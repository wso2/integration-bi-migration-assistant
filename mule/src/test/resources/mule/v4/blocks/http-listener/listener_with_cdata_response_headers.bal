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

public listener http:Listener config = new (8081);

service http:InterceptableService /mule4 on config {
    function init() returns error? {
    }

    public function createInterceptors() returns [MuleResponseErrorInterceptor0] {
        return [new MuleResponseErrorInterceptor0()];
    }

    resource function get demo(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        log:printInfo("xxx: logger invoked");

        http:Response response = <http:Response>ctx.attributes.response;
        response.setPayload(ctx.payload);

        // http response headers
        anydata responseHeaderValues = {
            "foo": "123456"
        };
        map<string> responseHeaders = check responseHeaderValues.cloneWithType();
        foreach [string, string] [headerName, headerValue] in responseHeaders.entries() {
            response.setHeader(headerName, headerValue);
        }

        // http response status code
        response.statusCode = 200;
        return response;
    }
}

service class MuleResponseErrorInterceptor0 {
    *http:ResponseErrorInterceptor;

    remote function interceptResponseError(http:RequestContext requestContext, http:Response interceptedResponse, error err) returns http:Response|error {
        Context ctx = {attributes: {response: interceptedResponse}};
        if ctx.payload !is () {
            (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        }

        // http response headers
        anydata responseHeaderValues = {
            "foo": "123456"
        };
        map<string> responseHeaders = check responseHeaderValues.cloneWithType();
        foreach [string, string] [headerName, headerValue] in responseHeaders.entries() {
            interceptedResponse.setHeader(headerName, headerValue);
        }

        // http response status code
        interceptedResponse.statusCode = 500;
        return <http:Response>ctx.attributes.response;
    }
}
