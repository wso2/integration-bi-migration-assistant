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

public listener http:Listener httpConfig = new (8081);

service / on httpConfig {
    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        do {
            log:printInfo("xxx: end of flow reached");
        } on fail error e {
            catch\-exception\-strategy(ctx, e);
        }

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}

public function catch\-exception\-strategy(Context ctx, error e) {
    log:printInfo("xxx: inside catch exception strategy");
}
