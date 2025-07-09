import ballerina/http;
import ballerina/log;

public type Context record {|
    anydata payload = ();
|};

public function demoFlow(Context ctx) {

    // http client request
    http:Client http_request_config = check new ("https://jsonplaceholder.typicode.com:80");
    http:Response clientResult0 = check http_request_config->/car\-posts/'12/honda\-civic/'991/'1\.1/latest.get();
    ctx.payload = check clientResult0.getJsonPayload();
    log:printInfo(string `Received from external API: ${ctx.payload.toString()}`);
}
