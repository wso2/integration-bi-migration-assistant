import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /healthcheck on httpListener {
    resource function get status(http:Caller caller) returns error? {
        Context ctx = {variables: {}, caller: caller};
        ctx.payload = {"status": "UP"};
        check respond(ctx);
    }
}
