import ballerina/http;
import ballerina/log;
import ballerina/uuid;

public type Context record {|
    anydata payload = ();
    string correlationId = uuid:createType4AsString();
|};

public function demoFlow(Context ctx) {

    // http client request
    http:Client http_request_config = check new ("jsonplaceholder.typicode.com:80");
    http:Response clientResult0 = check http_request_config->/posts/latest.get(headers = {"X-Correlation-ID": ctx.correlationId});
    ctx.payload = check clientResult0.getJsonPayload();
    log:printInfo(string `Received from external API: ${ctx.payload.toString()}`);
}
