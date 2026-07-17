import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /prop on httpListener {
    resource function get run() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        ctx.variables.temp = "value";
        ctx.variables.temp = ();
        response.setPayload({"done": true});
        return response;
    }
}
