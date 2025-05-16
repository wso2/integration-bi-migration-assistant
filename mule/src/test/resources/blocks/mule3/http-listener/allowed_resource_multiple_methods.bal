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

service /mule3 on config {
    resource function get .(http:Request request) returns http:Response|error {
        return invokeEndPoint0(request);
    }

    resource function post .(http:Request request) returns http:Response|error {
        return invokeEndPoint0(request);
    }

    resource function delete .(http:Request request) returns http:Response|error {
        return invokeEndPoint0(request);
    }
}

public function invokeEndPoint0(http:Request request) returns http:Response|error {
    Context ctx = {inboundProperties: {request, response: new}};
    log:printInfo("xxx: logger invoked");

    ctx.inboundProperties.response.setPayload(ctx.payload);
    return ctx.inboundProperties.response;
}
