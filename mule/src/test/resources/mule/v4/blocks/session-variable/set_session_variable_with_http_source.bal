import ballerina/http;
import ballerina/log;

public type SessionVars record {|
    string sessionVarExample?;
|};

public type InboundProperties record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    SessionVars sessionVars = {};
    InboundProperties inboundProperties;
|};

public listener http:Listener HTTP_Config = new (8081);

service /mule3 on HTTP_Config {
    resource function get session(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        ctx.sessionVars.sessionVarExample = "Initial Value";
        log:printInfo(string `Session Variable (Initial): ${ctx.sessionVars.sessionVarExample.toString()}`);
        ctx.sessionVars.sessionVarExample = "Modified Value";
        log:printInfo(string `Session Variable (Modified): ${ctx.sessionVars.sessionVarExample.toString()}`);

        // set payload
        string payload0 = "{\"message\":\"Check logs for session variable values\"}";
        ctx.payload = payload0;

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
