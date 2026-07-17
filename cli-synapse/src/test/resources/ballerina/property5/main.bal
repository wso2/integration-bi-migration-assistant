import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /chain on httpListener {
    resource function get run() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        foo(ctx);
        return response;
    }
}
