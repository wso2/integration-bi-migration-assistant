import ballerina/http;
import ballerina/log;

public type Vars record {|
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
        anydata responseHeaderValues = {"x-correlation-id": getOptionalHeader(<http:Request>ctx.attributes.request, "x-correlation-id") ?: "none"};
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
        if ctx.payload !is () {
            (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        }

        // http response headers

        // TODO: failed to convert #["x-correlation-id" : attributes.headers.'x-correlation-id' default "none"]

        // http response status code
        interceptedResponse.statusCode = check int:fromString((ctx.vars?.httpStatus ?: 500).toString());
        return <http:Response>ctx.attributes.response;
    }
}

function getOptionalHeader(http:Request req, string name) returns string? {
    string|http:HeaderNotFoundError value = req.getHeader(name);
    return value is string ? value : ();
}
