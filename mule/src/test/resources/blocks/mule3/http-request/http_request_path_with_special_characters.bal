import ballerina/http;
import ballerina/log;

public type Context record {|
    anydata payload;
|};

public function callExternalApiFlow(Context ctx) {

    // http client request
    http:Client HTTP_Request_Config = check new ("jsonplaceholder.typicode.com:80");
    http:Response clientResult0 = check HTTP_Request_Config->/car\-posts/'12/honda\-civic/'991/'1\.1/latest.get();
    ctx.payload = check clientResult0.getJsonPayload();
    log:printInfo(string `Received from external API: ${ctx.payload.toString()}`);
}
