import ballerina/http;

configurable int dispatchInboundPort = 8095;
configurable string dispatchInboundHost = "0.0.0.0";

public listener http:Listener dispatchInboundListener = new (dispatchInboundPort, {host: dispatchInboundHost});

public listener http:Listener httpListener = new (8080);

service /orders on httpListener {
    resource function get status(http:Caller caller) returns error? {
        Context ctx = {variables: {}, caller: caller};
        do {
            ctx.payload = {"status": "UP"};
            check respond(ctx);
        } on fail error err {
        }
    }
}
