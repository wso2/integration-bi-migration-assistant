import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /api on httpListener {
    resource function get unittest() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        response.setHeader("HTTP_SC", "200");
        ctx.payload = "GET RESPONSE";
        response.setPayload("GET RESPONSE");
        return response;
    }

    resource function post unittest() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        response.statusCode = 201;
        ctx.payload = "POST RESPONSE";
        response.setPayload("POST RESPONSE");
        return response;
    }

    resource function put unittest() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        response.statusCode = 201;
        ctx.payload = "PUT RESPONSE";
        response.setPayload("PUT RESPONSE");
        return response;
    }

    resource function delete unittest() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        response.setHeader("HTTP_SC", "200");
        ctx.payload = "DELETE RESPONSE";
        response.setPayload("DELETE RESPONSE");
        return response;
    }
}
