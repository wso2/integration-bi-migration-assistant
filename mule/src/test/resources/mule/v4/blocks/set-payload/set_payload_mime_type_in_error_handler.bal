import ballerina/log;

public type Context record {|
    anydata payload = ();
|};

public function demoFlow(Context ctx) {
    do {
        log:printInfo("xxx: in flow");
    } on fail error err {
        jsonErrorHandler(ctx, err);
    }
}

public function jsonErrorHandler(Context ctx, error err) {
    // on-error-continue

    // set payload
    json payload0 = toJsonPayload(ctx.payload);
    ctx.payload = payload0;
}

public function toJsonPayload(anydata payload) returns json {
    // Like Mule, text that is not JSON, or is a JSON string, is kept as it is
    if payload is string {
        json|error parsed = payload.fromJsonString();
        return parsed is json && parsed !is string ? parsed : payload;
    }
    return payload.toJson();
}
