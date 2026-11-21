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

public type APP__FAILED distinct error;

public listener http:Listener HTTP_Listener_config = new (8081);

service / on HTTP_Listener_config {
    function init() returns error? {
    }

    resource function default orders(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        do {
            log:printInfo("Looking up order");
        } on fail error err {
            // on-error-propagate
            log:printError("Order lookup failed");
            fail error APP__FAILED("Order lookup failed");
        }

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
