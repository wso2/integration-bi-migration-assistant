import ballerina/http;
import ballerina/log;

public type Vars record {|
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

service / on config {
    function init() returns error? {
    }

    resource function get vars(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        json logMessage0 = _dwMethod(ctx);
        log:printInfo(logMessage0.toJsonString());

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

public function _dwMethod(Context ctx) returns json => ctx.vars;
