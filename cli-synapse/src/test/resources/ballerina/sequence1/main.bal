import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /HelloWorld on httpListener {
    resource function get status/[string name]/[string id](string q) returns http:Response {
        Context ctx = {};
        http:Response response = new;
        response.setPayload({"Hello": "World"});
        sequence(ctx);
        return response;
    }
}
