import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /hello on httpListener {
    resource function get greet() returns http:Response {
        http:Response response = new;
        response.setPayload({"msg": "hello"});
        greetMediator(response, "en");
        return response;
    }
}
