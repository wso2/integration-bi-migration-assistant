import ballerina/http;
import ballerina/log;

public type Vars record {|
    int marks?;
|};

public type Attributes record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
    Attributes attributes;
|};

public listener http:Listener config = new (8081);

service /mule4 on config {
    resource function get choice(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        ctx.vars.marks = 73;
        if ctx.vars?.marks >= 75 {
            log:printInfo(string `You have scored ${ctx.vars?.marks.toString()}. Your grade is 'A'.`);
        } else if ctx.vars?.marks >= 65 {
            log:printInfo(string `You have scored ${ctx.vars?.marks.toString()}. Your grade is 'B'.`);
        } else if ctx.vars?.marks >= 55 {
            log:printInfo(string `You have scored ${ctx.vars?.marks.toString()}. Your grade is 'C'.`);
        } else {
            log:printInfo(string `You have scored ${ctx.vars?.marks.toString()}. Your grade is 'F'.`);
        }

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}
