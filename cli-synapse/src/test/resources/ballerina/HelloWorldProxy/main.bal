import ballerina/http;
import ballerina/log;

// Converted from Synapse proxy service `HelloWorldProxy`.
service /HelloWorldProxy on new http:Listener(8290) {

    resource function 'default .() returns json {
        log:printInfo("HelloWorldProxy invoked");
        json response = {"message": "Hello, World!"};
        return response;
    }
}
