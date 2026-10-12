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

public type APIKIT__NOT_FOUND distinct error;

public listener http:Listener listener\-config = new (8081);

service http:InterceptableService / on listener\-config {
    function init() returns error? {
    }

    public function createInterceptors() returns [MuleResponseErrorInterceptor0, MuleResponseInterceptor0] {
        return [new MuleResponseErrorInterceptor0(), new MuleResponseInterceptor0()];
    }

    resource function default [string... path](http:Request request) returns http:Response|error {
        return error APIKIT__NOT_FOUND("APIKIT:NOT_FOUND");
    }

    resource function get orders/[string id](http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {id}}};

        // set payload
        string payload3 = "B4";
        ctx.payload = payload3;

        // set payload
        string payload4 = "B2";
        ctx.payload = payload4;

        http:Response response = <http:Response>ctx.attributes.response;
        response.setPayload(ctx.payload);
        return response;
    }
}

service class MuleResponseErrorInterceptor0 {
    *http:ResponseErrorInterceptor;

    remote function interceptResponseError(http:RequestContext requestContext, http:Response interceptedResponse, error err) returns http:Response|error {
        Context ctx = {attributes: {response: interceptedResponse}};
        // on-error-continue
        log:printInfo("Handle any error");

        // set payload
        string payload1 = "B1";
        ctx.payload = payload1;
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

        // set payload
        string payload2 = "B2";
        ctx.payload = payload2;
        if ctx.payload !is () {
            (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        }
        return response;
    }
}
