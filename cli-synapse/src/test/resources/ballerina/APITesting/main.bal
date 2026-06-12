import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /api on httpListener {
    resource function get unittest() returns http:Response {
        http:Response response = new;
        response.setPayload("GET RESPONSE");
        return response;
    }

    resource function post unittest() returns http:Response {
        http:Response response = new;
        response.setPayload("POST RESPONSE");
        return response;
    }

    resource function put unittest() returns http:Response {
        http:Response response = new;
        response.setPayload("PUT RESPONSE");
        return response;
    }

    resource function delete unittest() returns http:Response {
        http:Response response = new;
        response.setPayload("DELETE RESPONSE");
        return response;
    }
}
