import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /HelloWorld on httpListener {
    resource function get payload() {
        http:Response response = new;
        Context ctx = {variables: {}};
        foo(ctx, response);
    }
}
