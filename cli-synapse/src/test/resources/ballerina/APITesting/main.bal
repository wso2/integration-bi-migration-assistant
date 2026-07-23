import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /api on httpListener {
    resource function get unittest(http:Caller caller) returns error? {
        Context ctx = {variables: {}, caller: caller};
        ctx.headers["HTTP_SC"] = "200";
        ctx.payload = "GET RESPONSE";
        check respond(ctx);
    }

    resource function post unittest(http:Caller caller, http:Request request) returns error? {
        Context ctx = {variables: {}, caller: caller};
        check emitPayload(ctx, request);
        ctx.statusCode = 201;
        ctx.payload = "POST RESPONSE";
        check respond(ctx);
    }

    resource function put unittest(http:Caller caller, http:Request request) returns error? {
        Context ctx = {variables: {}, caller: caller};
        check emitPayload(ctx, request);
        ctx.statusCode = 201;
        ctx.payload = "PUT RESPONSE";
        check respond(ctx);
    }

    resource function delete unittest(http:Caller caller, http:Request request) returns error? {
        Context ctx = {variables: {}, caller: caller};
        check emitPayload(ctx, request);
        ctx.headers["HTTP_SC"] = "200";
        ctx.payload = "DELETE RESPONSE";
        check respond(ctx);
    }
}
