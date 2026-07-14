import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /HelloWorld on httpListener {
    resource function get orders() returns http:Response {
        http:Response response = new;
        Context ctx = {};
        ctx.before = "before";
        response.setPayload({"id": "001"});
        ctx.after = "after";
        return response;
    }
}
