import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /prop on httpListener {
    resource function get run(http:Caller caller) returns error? {
        Context ctx = {variables: {}, caller: caller};
        ctx.variables.temp = "value";
        ctx.variables.temp = ();
        ctx.payload = {"done": true};
        check respond(ctx);
    }
}
