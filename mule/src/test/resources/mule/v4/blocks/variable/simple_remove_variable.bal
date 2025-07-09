import ballerina/http;
import ballerina/log;

public type Vars record {|
    string greeting?;
    string 'from?;
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
    resource function get remove_variable(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        ctx.vars.greeting = "Hello";
        ctx.vars.'from = "USA";
        log:printInfo(string `Variables before removing: greeting - ${ctx.vars.greeting.toString()}, from - ${ctx.vars.'from.toString()}`);
        ctx.vars.greeting = ();
        ctx.vars.'from = ();
        log:printInfo(string `Variables after removing: greeting - ${ctx.vars.greeting.toString()}, from - ${ctx.vars.'from.toString()}`);

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}
