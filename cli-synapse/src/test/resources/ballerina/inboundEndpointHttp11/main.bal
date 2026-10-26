import ballerina/http;

configurable int complexPatternInboundPort = 8098;
configurable string complexPatternInboundHost = "0.0.0.0";

public listener http:Listener complexPatternInboundListener = new (complexPatternInboundPort, {host: complexPatternInboundHost});

public listener http:Listener httpListener = new (8080);

service /aaaaaaaaaa on httpListener {
    resource function get status(http:Caller caller) returns error? {
        Context ctx = {variables: {}, caller: caller};
        do {
            ctx.payload = {"status": "UP"};
            check respond(ctx);
        } on fail error err {
        }
    }
}
