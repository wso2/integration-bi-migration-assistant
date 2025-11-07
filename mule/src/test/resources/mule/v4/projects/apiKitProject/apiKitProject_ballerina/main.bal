import ballerina/http;
import ballerina/log;

public listener http:Listener http\-listener\-config = new (8080);

service / on http\-listener\-config {
    resource function get orders/[string id](http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {id}}};
        log:printInfo(string `Received order id: ${id.toString()}`);

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}
