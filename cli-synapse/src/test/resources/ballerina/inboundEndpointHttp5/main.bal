import ballerina/http;
import ballerina/log;

configurable int httpInboundUnresolvedOnErrorPort = 8090;
configurable string httpInboundUnresolvedOnErrorHost = "0.0.0.0";

public listener http:Listener httpInboundUnresolvedOnErrorListener = new (httpInboundUnresolvedOnErrorPort, {host: httpInboundUnresolvedOnErrorHost});

service / on httpInboundUnresolvedOnErrorListener {
    resource function 'default [string... path](http:Caller caller, http:Request request) returns error? {
        Context ctx = {variables: {}, caller: caller};
        // TODO: Unresolved Synapse fault sequence reference 'missingFaultSeq' (from inboundEndpoint.xml). Referenced fault sequence 'missingFaultSeq' was not found among the converted artifacts; falling back to the default error handler.
        do {
            check emitPayload(ctx, request);
            check mainFlow(ctx);
        } on fail error err {
            log:printError("Unhandled error in mediation", 'error = err);
            ctx.payload = {"error": err.message()};
            ctx.statusCode = 500;
            check respond(ctx);
        }
    }
}
