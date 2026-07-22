import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /echo on httpListener {
    resource function get message(http:Caller caller) returns error? {
        Context ctx = {variables: {}, caller: caller};
        check respond(ctx);
    }

    resource function post message(http:Caller caller, http:Request request) returns error? {
        Context ctx = {variables: {}, caller: caller};
        check emitPayload(ctx, request);
        check respond(ctx);
    }
}
