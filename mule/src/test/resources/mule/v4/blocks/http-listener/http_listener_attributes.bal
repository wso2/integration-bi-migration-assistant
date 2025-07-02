import ballerina/http;

public type FlowVars record {|
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

public type InboundProperties record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    FlowVars flowVars = {};
    InboundProperties inboundProperties;
|};

public listener http:Listener config = new (8081);

service /mule4 on config {
    resource function get attribute_test/[string country]/v1(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new, uriParams: {country}}};
        ctx.flowVars.queryParams = ctx.inboundProperties.request.getQueryParams();
        ctx.flowVars.queryParams2 = ctx.inboundProperties.request.getQueryParams();
        ctx.flowVars.city = ctx.inboundProperties.request.getQueryParamValue("city");
        ctx.flowVars.city2 = ctx.inboundProperties.request.getQueryParamValue("city");
        ctx.flowVars.uriParams = ctx.inboundProperties.uriParams;
        ctx.flowVars.country = ctx.inboundProperties.uriParams.get("country");
        ctx.flowVars.unsupportedAttribute = ctx.inboundProperties["unsupportedAttribute"];
        ctx.flowVars.unsupportedAttributeAccess = ctx.inboundProperties["unsupportedAttribute"].city;
        ctx.flowVars.httpMethod = ctx.inboundProperties.request.method;

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
