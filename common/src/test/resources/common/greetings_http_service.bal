import ballerina/http;
import ballerina/log;

public listener http:Listener myHttpListener = new (9090);

service /greetings on myHttpListener {
    resource function get hello(string name) returns json {
        log:printInfo("Received request for greeting with name: " + name);
        json payload = {"message": "Hello " + name};
        return payload;
    }
}

// This Ballerina service listens on port 9090 and provides a resource to greet users.
// e.g. curl -X GET http://localhost:9090/greetings/hello?name=John
