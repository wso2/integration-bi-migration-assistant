import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /syn on httpListener {
    resource function get r() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        ctx.variables.synProp = "hi";
        ctx.variables.defProp = 7;
        return response;
    }
}
