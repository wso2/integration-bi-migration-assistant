import ballerina/http;
import ballerina/log;

public type InboundProperties record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    InboundProperties inboundProperties;
|};

public listener http:Listener config = new (8081);

service /mule3 on config {
    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        do {
            log:printInfo("xxx: logger invoked via http end point");
        } on fail error e {
            // TODO: if conditions may require some manual adjustments
            if e.message() == "java.lang.NullPointerException" {
                log:printInfo("xxx: first catch condition invoked");
            } else if e.message() == "java.lang.IllegalArgumentException" || e.message() == "java.lang.IllegalStateException" {
                log:printInfo("xxx: second catch condition invoked");
            } else {
                log:printInfo("xxx: generic catch condition invoked");
            }
        }

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
