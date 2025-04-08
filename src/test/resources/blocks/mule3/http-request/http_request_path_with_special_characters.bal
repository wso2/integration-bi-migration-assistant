import ballerina/http;
import ballerina/log;

type Context record {|
    anydata payload;
|};

function callExternalApiFlow(Context ctx) {

    // http client request
    http:Client HTTP_Request_Config = check new ("jsonplaceholder.typicode.com:80");
    http:Response _clientResult0_ = check HTTP_Request_Config->/car\-posts/[12]/honda\-civic/[991]/latest.get();
    ctx.payload = check _clientResult0_.getJsonPayload();
    log:printInfo(string `Received from external API: ${ctx.payload.toString()}`);
}
