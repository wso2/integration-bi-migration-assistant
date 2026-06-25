import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /HelloWorld on httpListener {
    resource function get status/[string name]/[string id](string q) returns http:Response {
        http:Response response = new;
        foo();
        response.setPayload({"Hello": "World"});
        return response;
    }

    resource function get id1/[string id]() returns http:Response {
        http:Response response = new;
        bar();
        response.setPayload({"Hello": "World"});
        return response;
    }
}
