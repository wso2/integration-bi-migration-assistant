import ballerina/http;
import ballerina/log;

public listener http:Listener httpConfig = new (8081);

service / on httpConfig {
    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        mainconfigSub_Flow(ctx);
        commonConfig1Flow(ctx);
        commonConfig2Sub_Flow(ctx);
        json scriptVar = check _dwMethod0_(ctx.payload.toJson());
        json inlineVar = check _dwMethod1_(ctx.payload.toJson());
        xml _dwOutput_ = _dwMethod2_(ctx.payload.toJson());
        ctx.payload = _dwOutput_;
        log:printInfo("xxx: end of the logger reached");

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
