import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /orders on httpListener {
    resource function get list(http:Caller caller) returns error? {
        Context ctx = {variables: {}, caller: caller};
        ctx.variables.prop1 = "list";
        ctx.variables.r1only = 1;
        ctx.payload = {"resource": "list"};
        check respond(ctx);
    }

    resource function post create(http:Caller caller, http:Request request) returns error? {
        Context ctx = {variables: {}, caller: caller};
        check emitPayload(ctx, request);
        ctx.variables.prop1 = "create";
        ctx.variables.r2only = true;
        ctx.payload = {"resource": "create"};
        check respond(ctx);
    }
}
