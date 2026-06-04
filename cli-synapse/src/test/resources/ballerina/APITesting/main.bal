import ballerina/http;

// Converted from WSO2 Synapse REST API `RESTApi` (context: /api).
// Each Synapse <resource> maps to a resource function. The `HTTP_SC` property
// becomes the response status code and the text payloadFactory format becomes
// the response text payload.
service /api on new http:Listener(8290) {

    resource function get unittest() returns http:Response {
        http:Response response = new;
        response.statusCode = 200;
        response.setTextPayload("GET RESPONSE");
        return response;
    }

    resource function post unittest() returns http:Response {
        http:Response response = new;
        response.statusCode = 201;
        response.setTextPayload("POST RESPONSE");
        return response;
    }

    resource function put unittest() returns http:Response {
        http:Response response = new;
        response.statusCode = 201;
        response.setTextPayload("PUT RESPONSE");
        return response;
    }

    resource function delete unittest() returns http:Response {
        http:Response response = new;
        response.statusCode = 200;
        response.setTextPayload("DELETE RESPONSE");
        return response;
    }
}