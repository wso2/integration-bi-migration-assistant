import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /HelloWorld on httpListener {
    resource function get chain(http:Caller caller) returns error? {
        Context ctx = {variables: {}, caller: caller};
        check foo(ctx);
    }
}
