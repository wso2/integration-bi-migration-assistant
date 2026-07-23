import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /syn on httpListener {
    resource function get r(http:Caller caller) returns error? {
        Context ctx = {variables: {}, caller: caller};
        ctx.variables.synProp = "hi";
        ctx.variables.defProp = 7;
        check respond(ctx);
    }
}
