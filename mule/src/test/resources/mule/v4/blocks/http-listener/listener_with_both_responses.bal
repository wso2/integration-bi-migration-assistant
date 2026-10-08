import ballerina/http;
import ballerina/log;

public type Vars record {|
    string httpStatus?;
    anydata outboundHeaders?;
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

public listener http:Listener config = new (8081);

service http:InterceptableService /mule4 on config {
    function init() returns error? {
    }

    public function createInterceptors() returns [MuleResponseErrorInterceptor0] {
        return [new MuleResponseErrorInterceptor0()];
    }

    resource function get demo(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        ctx.vars.httpStatus = "202";
        log:printInfo("xxx: logger invoked");

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

        // set payload
        anydata payload0 = ctx.payload;
        ctx.payload = payload0;
        if ctx.payload !is () {
            (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        }

        // http response status code
        interceptedResponse.statusCode = check int:fromString((ctx.vars?.httpStatus ?: 500).toString());
        return <http:Response>ctx.attributes.response;
    }
}
