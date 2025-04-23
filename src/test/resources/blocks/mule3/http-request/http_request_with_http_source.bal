import ballerina/http;
import ballerina/log;

public type InboundProperties record {|
    http:Response response;
    http:Request request;
    map<string> uriParams;
|};

public type Context record {|
    anydata payload;
    InboundProperties inboundProperties;
|};

public listener http:Listener HTTP_Listener_Config = new (8081, {host: "0.0.0.0"});

service /mule3 on HTTP_Listener_Config {
    Context ctx;

    function init() {
        self.ctx = {payload: (), inboundProperties: {response: new, request: new, uriParams: {}}};
    }

    resource function get .(http:Request request) returns http:Response|error {
        self.ctx.inboundProperties.request = request;
        return _invokeEndPoint0_(self.ctx);
    }
}

public function _invokeEndPoint0_(Context ctx) returns http:Response|error {

    // http client request
    http:Client HTTP_Request_Config = check new ("jsonplaceholder.typicode.com:80");
    http:Response _clientResult0_ = check HTTP_Request_Config->/posts/latest.get();
    ctx.payload = check _clientResult0_.getJsonPayload();
    log:printInfo(string `Received from external API: ${ctx.payload.toString()}`);

    ctx.inboundProperties.response.setPayload(ctx.payload);
    return ctx.inboundProperties.response;
}
