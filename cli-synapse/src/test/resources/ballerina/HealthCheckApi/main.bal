import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /healthcheck on httpListener {
    resource function get status() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        ctx.payload = {"status": "UP"};
        response.setPayload({"status": "UP"});
        return response;
    }
}
