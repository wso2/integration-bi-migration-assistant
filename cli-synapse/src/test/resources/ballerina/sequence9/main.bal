import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /HelloWorld on httpListener {
    resource function get orders() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        ctx.variables.before = "before";
        ctx.payload = {"id": "001"};
        response.setPayload({"id": "001"});
        ctx.variables.after = "after";
        return response;
    }
}
