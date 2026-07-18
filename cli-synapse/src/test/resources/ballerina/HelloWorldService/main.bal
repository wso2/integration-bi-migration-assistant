import ballerina/http;

public listener http:Listener httpListener = new (8080);

service /HelloWorld on httpListener {
    resource function get .() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        ctx.payload = {"Hello": "World"};
        response.setPayload({"Hello": "World"});
        return response;
    }

    resource function get status() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        ctx.payload = {"Hello": "World"};
        response.setPayload({"Hello": "World"});
        return response;
    }

    resource function get status/[string id]() returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        ctx.payload = {"Hello": "World"};
        response.setPayload({"Hello": "World"});
        return response;
    }

    resource function get status/[string name]/[string id](string q) returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        ctx.payload = {"Hello": "World"};
        response.setPayload({"Hello": "World"});
        return response;
    }

    resource function get .(string p) returns http:Response {
        http:Response response = new;
        Context ctx = {variables: {}};
        ctx.payload = {"Hello": "World"};
        response.setPayload({"Hello": "World"});
        return response;
    }
}
