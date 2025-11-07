import ballerina/http;
import ballerina/log;

public type Vars record {|
    string name?;
    int age?;
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
    resource function get set_variable(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        ctx.vars.name = "John";
        ctx.vars.age = 29;
        ctx.vars.'from = "USA";
        log:printInfo(string `Variables defined are: name - ${ctx.vars?.name.toString()}, age - ${ctx.vars?.age.toString()}, from - ${ctx.vars?.'from.toString()}`);

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}
