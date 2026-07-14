import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /orders on httpListener {
    resource function get list() returns http:Response {
        http:Response response = new;
        Context ctx = {};
        ctx.prop1 = "list";
        ctx.r1only = 1;
        response.setPayload({"resource": "list"});
        return response;
    }

    resource function post create() returns http:Response {
        http:Response response = new;
        Context ctx = {};
        ctx.prop1 = 55;
        ctx.r2only = true;
        response.setPayload({"resource": "create"});
        return response;
    }
}
