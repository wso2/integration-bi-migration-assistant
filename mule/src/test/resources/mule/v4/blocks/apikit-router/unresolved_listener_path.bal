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

    resource function get \$\{http\.listener\.path\}(http:Request request) returns http:Response|error {
        // TODO: The APIkit router listener path '${http.listener.path}' is not a fixed path, so the APIkit resources of this service are generated without it as their path prefix
        return error APIKIT__NOT_FOUND("APIKIT:NOT_FOUND");
    }

    resource function get orders/[string id](http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {id}}};
        log:printInfo("Get order");

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
