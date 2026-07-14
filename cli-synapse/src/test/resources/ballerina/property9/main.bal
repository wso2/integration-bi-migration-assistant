import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /syn on httpListener {
    resource function get r() returns http:Response {
        http:Response response = new;
        Context ctx = {};
        ctx.synProp = "hi";
        ctx.defProp = 7;
        return response;
    }
}
