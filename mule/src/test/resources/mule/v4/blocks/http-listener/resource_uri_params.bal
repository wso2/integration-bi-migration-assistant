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

service /mule4 on config {
    function init() returns error? {
    }

    resource function get [string version]/demo/[string id](http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {version, id}}};
        log:printInfo("xxx: logger invoked");
        log:printInfo(string `Path params - version: ${ctx.attributes.uriParams.get("version").toString()}, id: ${ctx.attributes.uriParams.get("id").toString()}`);

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
