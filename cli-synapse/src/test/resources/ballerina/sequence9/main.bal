import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /HelloWorld on httpListener {
    resource function get orders(http:Caller caller) returns error? {
        Context ctx = {variables: {}, caller: caller};
        ctx.variables.before = "before";
        ctx.payload = {"id": "001"};
        ctx.variables.after = "after";
        check respond(ctx);
    }
}
