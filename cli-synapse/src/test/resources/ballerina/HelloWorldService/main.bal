import ballerina/http;

service /HelloWorld on new http:Listener(8290) {

    resource function get status() returns json {
        return {"Hello":"World"};
    }
}
