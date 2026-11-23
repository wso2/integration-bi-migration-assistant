import ballerina/http;

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

    resource function get set_payload(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};

        // set payload
        json payload0 = {"status": "ok", "count": 2, "ratio": 0.5, "tags": ["a", "b"], "next": null};
        ctx.payload = payload0;

        // set payload
        json payload1 = ["Alice", "Bob"];
        ctx.payload = payload1;

        // set payload
        json payload2 = {"msg": "line1\nline2 \"quoted\""};
        ctx.payload = payload2;

        // set payload
        json payload3 = toJsonPayload(ctx.payload);
        ctx.payload = payload3;

        // set payload
        // TODO: value is not valid JSON, so it is kept as a string although mimeType is application/json
        string payload4 = "not json";
        ctx.payload = payload4;

        // set payload
        string payload5 = "plain text";
        ctx.payload = payload5;

        // set payload
        json payload6 = {"hundred": 100, "half": 0.5};
        ctx.payload = payload6;

        // set payload
        json payload7 = toJsonPayload("{\"huge\": 1e400}");
        ctx.payload = payload7;

        // set payload
        json payload8 = toJsonPayload("{\"precise\": 0.10000000000000000001}");
        ctx.payload = payload8;

        // set payload
        json payload9 = toJsonPayload("{\"lone\": \"\\uD800\"}");
        ctx.payload = payload9;
        jsonSubFlow(ctx);

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

public function jsonSubFlow(Context ctx) {

    // set payload
    json payload10 = toJsonPayload(ctx.payload);
    ctx.payload = payload10;
}

public function toJsonPayload(anydata payload) returns json {
    // Like Mule, a payload that is not JSON text is kept as it is
    if payload is string {
        json|error parsed = payload.fromJsonString();
        return parsed is json ? parsed : payload;
    }
    return payload.toJson();
}
