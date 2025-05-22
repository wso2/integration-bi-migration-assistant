import ballerina/http;
import ballerina/log;

public type FlowVars record {|
    string name?;
    string age?;
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

service /mule3 on config {
    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        ctx.flowVars.name = "John";
        ctx.flowVars.age = "29";
        log:printInfo(string `Variables defined are: name - ${ctx.flowVars.name.toString()}, age - ${ctx.flowVars.age.toString()}`);

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
