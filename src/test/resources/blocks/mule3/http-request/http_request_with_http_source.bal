import ballerina/http;
import ballerina/log;

type InboundProperties record {|
    http:Response response;
|};

type Context record {|
    anydata payload;
    InboundProperties inboundProperties;
|};

listener http:Listener HTTP_Listener_Config = new (8081, {host: "0.0.0.0"});

service /mule3 on HTTP_Listener_Config {
    Context ctx;

    function init() {
        self.ctx = {payload: (), inboundProperties: {response: new}};
    }

    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    private function _invokeEndPoint0_(Context ctx) returns http:Response|error {

        // http client request
        http:Client HTTP_Request_Config = check new ("jsonplaceholder.typicode.com:80");
        http:Response _clientResult0_ = check HTTP_Request_Config->/posts/latest.get();
        ctx.payload = check _clientResult0_.getJsonPayload();
        log:printInfo(string `Received from external API: ${ctx.payload.toString()}`);
        return ctx.inboundProperties.response;
    }
}
