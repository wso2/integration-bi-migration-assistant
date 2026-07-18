import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /orders on httpListener {
    resource function get list() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        ctx.variables.prop1 = "list";
        ctx.variables.r1only = 1;
        ctx.payload = {"resource": "list"};
        response.setPayload({"resource": "list"});
        return response;
    }

    resource function post create() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        ctx.variables.prop1 = "create";
        ctx.variables.r2only = true;
        ctx.payload = {"resource": "create"};
        response.setPayload({"resource": "create"});
        return response;
    }
}
