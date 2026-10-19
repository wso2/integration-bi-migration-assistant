import ballerina/http;
import ballerina/log;

public type Vars record {|
    anydata plainHeader?;
    anydata singleQuotedHeader?;
    anydata doubleQuotedHeader?;
    anydata bracketHeader?;
    anydata doubleQuotedBracketHeader?;
    anydata keywordHeader?;
    anydata headerWithDefault?;
    anydata isCrmIdEmpty?;
    string country?;
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

service /mule4 on config {
    function init() returns error? {
    }

    resource function get orders/[string country](http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {country}}};
        ctx.vars.plainHeader = getOptionalHeader(<http:Request>ctx.attributes.request, "orderType");
        ctx.vars.singleQuotedHeader = getOptionalHeader(<http:Request>ctx.attributes.request, "order-type");
        ctx.vars.doubleQuotedHeader = getOptionalHeader(<http:Request>ctx.attributes.request, "order-type");
        ctx.vars.bracketHeader = getOptionalHeader(<http:Request>ctx.attributes.request, "order-type");
        ctx.vars.doubleQuotedBracketHeader = getOptionalHeader(<http:Request>ctx.attributes.request, "order-type");
        ctx.vars.keywordHeader = getOptionalHeader(<http:Request>ctx.attributes.request, "type");
        ctx.vars.headerWithDefault = getOptionalHeader(<http:Request>ctx.attributes.request, "x-correlation-id") ?: "none";
        ctx.vars.isCrmIdEmpty = getOptionalHeader(<http:Request>ctx.attributes.request, "crmId").toString() == "";
        ctx.vars.country = ctx.attributes.uriParams.get("country");
        // TODO: failed to convert #[attributes.headers]
        log:printInfo(string `Order type: ${getOptionalHeader(<http:Request>ctx.attributes.request, "orderType").toString()}`);

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

function getOptionalHeader(http:Request req, string name) returns string? {
    string|http:HeaderNotFoundError value = req.getHeader(name);
    return value is string ? value : ();
}
