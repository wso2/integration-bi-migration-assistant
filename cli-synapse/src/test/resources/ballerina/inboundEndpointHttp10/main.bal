import ballerina/http;

configurable int invalidPatternInboundPort = 8096;
configurable string invalidPatternInboundHost = "0.0.0.0";

public listener http:Listener invalidPatternInboundListener = new (invalidPatternInboundPort, {host: invalidPatternInboundHost});

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
