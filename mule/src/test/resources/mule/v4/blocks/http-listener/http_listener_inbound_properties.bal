import ballerina/http;

public type FlowVars record {|
    map<string[]> queryParams?;
    string city?;
    map<string[]> queryParams2?;
    string city2?;
    map<string> uriParams?;
    string country?;
    anydata unsupportedProperty?;
    anydata unsupportedPropertyAccess?;
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

public listener http:Listener HTTP_Listener_Configuration = new (8081);

service / on HTTP_Listener_Configuration {
    resource function get proptest/[string country]/v1(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new, uriParams: {country}}};
        ctx.flowVars.queryParams = ctx.inboundProperties.request.getQueryParams();
        ctx.flowVars.city = ctx.inboundProperties.request.getQueryParamValue("city");
        ctx.flowVars.queryParams2 = ctx.inboundProperties.request.getQueryParams();
        ctx.flowVars.city2 = ctx.inboundProperties.request.getQueryParamValue("city");
        ctx.flowVars.uriParams = ctx.inboundProperties.uriParams;
        ctx.flowVars.country = ctx.inboundProperties.uriParams.get("country");
        ctx.flowVars.unsupportedProperty = ctx.inboundProperties["unsupported.property"];
        ctx.flowVars.unsupportedPropertyAccess = ctx.inboundProperties["unsupported.property"].city;
        ctx.flowVars.httpMethod = ctx.inboundProperties.request.method;

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
