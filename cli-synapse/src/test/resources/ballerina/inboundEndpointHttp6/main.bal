import ballerina/http;

configurable int suspendedHttpInboundPort = 8085;
configurable string suspendedHttpInboundHost = "0.0.0.0";

public listener http:Listener suspendedHttpInboundListener = new (suspendedHttpInboundPort, {host: suspendedHttpInboundHost});

service / on suspendedHttpInboundListener {
    resource function 'default [string... path](http:Caller caller, http:Request request) returns error? {
        Context ctx = {variables: {}, caller: caller};
        do {
            check emitPayload(ctx, request);
            check foo(ctx);
        } on fail error err {
            ctx.variables.ERROR_MESSAGE = err.message();
            check handleError(ctx);
        }
    }
}
