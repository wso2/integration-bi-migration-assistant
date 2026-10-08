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

public listener http:Listener listener\-config = new (8080);

service / on listener\-config {
    function init() returns error? {
    }

    resource function default [string... path](http:Request request) returns http:Response|error {
        return error APIKIT__NOT_FOUND("APIKIT:NOT_FOUND");
    }

    resource function get orders/[string id](http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {id}}};
        log:printInfo("Get order");

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }

    resource function post orders(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        log:printInfo("Create order");

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
