import ballerina/http;
import ballerina/log;

public type FlowVars record {|
    string greeting?;
    string 'from?;
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
    resource function get remove_variable(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        ctx.flowVars.greeting = "Hello";
        ctx.flowVars.'from = "USA";
        log:printInfo(string `Variables before removing: greeting - ${ctx.flowVars.greeting.toString()}, from - ${ctx.flowVars.'from.toString()}`);
        ctx.flowVars.greeting = ();
        ctx.flowVars.'from = ();
        log:printInfo(string `Variables after removing: greeting - ${ctx.flowVars.greeting.toString()}, from - ${ctx.flowVars.'from.toString()}`);

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
