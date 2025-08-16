import ballerina/http;
import ballerina/log;

public type Attributes record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

public listener http:Listener http_listener = new (9090);

service / on http_listener {
    resource function default first_successful(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};

        // first-successful sequential route execution
        anydata firstSuccessfulResult0 = check firstSuccessful0(ctx);
        ctx.payload = firstSuccessfulResult0;

        log:printInfo(ctx.payload.toString());

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}

public function route1(Context ctx) returns anydata|error {
    // Route 1

    // set payload
    string payload1 = "Route 1 completed";
    ctx.payload = payload1;
    return ctx.payload;
}

public function firstSuccessful0(Context ctx) returns anydata|error {
    anydata|error r0 = route0(ctx);
    if r0 !is error {
        return r0;
    }
    anydata|error r1 = route1(ctx);
    if r1 !is error {
        return r1;
    }
    anydata|error r2 = route2(ctx);
    if r2 !is error {
        return r2;
    }
    return error("All routes failed", r2);
}

public function route2(Context ctx) returns anydata|error {
    // Route 2

    // set payload
    string payload2 = "Route 2 completed";
    ctx.payload = payload2;
    return ctx.payload;
}

public function route0(Context ctx) returns anydata|error {
    // Route 0

    // set payload
    string payload0 = "Route 0 completed";
    ctx.payload = payload0;
    return ctx.payload;
}
