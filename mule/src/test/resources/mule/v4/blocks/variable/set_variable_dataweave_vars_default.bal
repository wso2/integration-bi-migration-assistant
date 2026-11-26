import ballerina/http;
import ballerina/log;

public type Vars record {|
    string externalId?;
    any searchId?;
    json 'order?;
    json orderId?;
    json order\-ref?;
    json orderRef?;
    json localValue?;
    json unsetId?;
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

    resource function get search/[string id](http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {id}}};
        any searchId = check setSearchid(ctx);
        ctx.vars.searchId = searchId;
        json orderId = check setSearchid1(ctx);
        ctx.vars.orderId = orderId;
        json orderRef = setSearchid2(ctx);
        ctx.vars.orderRef = orderRef;
        json localValue = check setSearchid3(ctx);
        ctx.vars.localValue = localValue;
        log:printInfo(ctx.vars?.searchId.toString());

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }

    resource function get 'external(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        ctx.vars.externalId = "ext-1";
        log:printInfo(ctx.vars?.unsetId.toString());

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

public function setSearchid(Context ctx) returns any|error => ctx.vars?.externalId ?: (check (ctx.attributes.uriParams.hasKey("id") ? ctx.attributes.uriParams.get("id") : error("no such uri param")));

public function setSearchid1(Context ctx) returns json|error => check ctx.vars?.'order?.id;

public function setSearchid2(Context ctx) returns json => ctx.vars?.order\-ref;

public function setSearchid3(Context ctx) returns json|error => let var indexMemberPairs = [{"a": "local"}].enumerate() in check indexMemberPairs.map(
    indexMemberPair => let int index = indexMemberPair[0], var vars = indexMemberPair[1] in vars.a
);
