import ballerina/http;

public type Vars record {|
    map<string[]> queryParams?;
    map<string[]> queryParams2?;
    string city?;
    string city2?;
    map<string> uriParams?;
    string country?;
    anydata unsupportedAttribute?;
    anydata unsupportedAttributeAccess?;
    anydata httpMethod?;
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

    resource function get attribute_test/[string country]/v1(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {country}}};
        ctx.vars.queryParams = ctx.attributes.request.getQueryParams();
        ctx.vars.queryParams2 = ctx.attributes.request.getQueryParams();
        ctx.vars.city = ctx.attributes.request.getQueryParamValue("city");
        ctx.vars.city2 = ctx.attributes.request.getQueryParamValue("city");
        ctx.vars.uriParams = ctx.attributes.uriParams;
        ctx.vars.country = ctx.attributes.uriParams.get("country");
        ctx.vars.unsupportedAttribute = ctx.attributes["unsupportedAttribute"];
        ctx.vars.unsupportedAttributeAccess = ctx.attributes["unsupportedAttribute"].city;
        ctx.vars.httpMethod = ctx.attributes.request.method;

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
