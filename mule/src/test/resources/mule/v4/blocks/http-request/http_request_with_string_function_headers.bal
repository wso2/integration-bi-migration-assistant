import ballerina/http;
import ballerina/log;
import ballerina/uuid;

public type Vars record {|
    string marketId?;
    string region?;
    string channel?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
    string correlationId = uuid:createType4AsString();
|};

public function demoFlow(Context ctx) {
    ctx.vars.marketId = "US";
    ctx.vars.region = "west";
    ctx.vars.channel = " web ";
    log:printInfo(string `region: ${ctx.vars?.region.toString().toUpperAscii()}`);

    // http client request
    http:Client http_request_config = check new ("orders.example.com:80");
    map<string?> headers_nilable = {
        "baseSiteId": ctx.vars?.marketId.toString().toLowerAscii(),
        "region": ctx.vars?.region.toString().toUpperAscii(),
        "channel": ctx.vars?.channel.toString().trim()
    };
    map<string> headers = map from string key in headers_nilable.keys()
        where headers_nilable.get(key) is string
        select [key, <string>headers_nilable.get(key)];
    if headers.keys().every(key => !key.equalsIgnoreCaseAscii("X-Correlation-ID")) {
        headers["X-Correlation-ID"] = ctx.correlationId;
    }
    http:Response clientResult0 = check http_request_config->/orders.get(headers);
    ctx.payload = check clientResult0.getJsonPayload();
}
