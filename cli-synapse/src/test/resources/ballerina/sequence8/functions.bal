import ballerina/http;

function foo(Context ctx, http:Response response) {
    ctx.payload = {"id": "002"};
    response.setPayload({"id": "002"});
}

function wrapper(Context ctx, http:Response response) {
    foo(ctx, response);
}
