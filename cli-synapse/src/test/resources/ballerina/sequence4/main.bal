import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /HelloWorld on httpListener {
    resource function get respond() returns http:Response {
        http:Response response = new;
        bar();
        return response;
    }
}
