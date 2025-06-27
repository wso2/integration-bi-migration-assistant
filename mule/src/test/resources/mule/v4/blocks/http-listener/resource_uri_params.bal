import ballerina/http;
import ballerina/log;

public type InboundProperties record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    InboundProperties inboundProperties;
|};

public listener http:Listener config = new (8081);

service /mule4 on config {
    resource function get [string version]/demo/[string id](http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new, uriParams: {version, id}}};
        log:printInfo("xxx: logger invoked");
        log:printInfo(string `Path params - version: ${ctx.inboundProperties.uriParams.get("version").toString()}, id: ${ctx.inboundProperties.uriParams.get("id").toString()}`);

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
