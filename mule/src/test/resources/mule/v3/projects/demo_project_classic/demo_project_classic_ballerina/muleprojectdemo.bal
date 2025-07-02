import ballerina/http;
import ballerina/log;

public listener http:Listener httpConfig = new (8081);

service / on httpConfig {
    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        mainconfigSub_Flow(ctx);
        commonConfig1Flow(ctx);
        commonConfig2Sub_Flow(ctx);
        // TODO: DataWeave script not found in path: projects/demo_project_classic/src/main/resources/dwlFiles/values.dwl
        json inlineVar = check _dwMethod0_(ctx.payload.toJson());
        // TODO: DataWeave script not found in path: projects/demo_project_classic/src/main/resources/dwlFiles/values_unsupport.dwl
        ctx.payload = _dwOutput_;
        log:printInfo("xxx: end of the logger reached");

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}

function _dwMethod0_(json payload) returns json|error {
    float conversionRate = 13.15;
    return {"s": "Hello World", "n": 1.23, "b": true, "a": check [1, 2, 3].ensureType(json), "o": check {"name": "Anne"}.ensureType(json)};
}
