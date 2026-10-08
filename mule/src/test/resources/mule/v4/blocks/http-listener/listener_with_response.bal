import ballerina/http;
import ballerina/log;

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

public listener http:Listener config = new (8081);

service /mule4 on config {
    function init() returns error? {
    }

    resource function get demo(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        log:printInfo("xxx: logger invoked");

        // set payload
        string payload0 = "created";
        ctx.payload = payload0;

        http:Response response = <http:Response>ctx.attributes.response;
        response.setPayload(ctx.payload);

        // http response headers
        anydata responseHeaderValues = {"Content-Type": "application/json", "x-source": "mule"};
        map<string> responseHeaders = check responseHeaderValues.cloneWithType();
        foreach [string, string] [headerName, headerValue] in responseHeaders.entries() {
            response.setHeader(headerName, headerValue);
        }

        // http response status code
        response.statusCode = 201;
        return response;
    }
}
