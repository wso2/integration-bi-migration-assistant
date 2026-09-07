import ballerina/log;

public type Context record {|
    anydata payload = ();
|};

public function log\-without\-output\-directive(Context ctx) {
    any logMessage0 = _dwMethod(ctx);
    log:printInfo(logMessage0.toString());
}

public function _dwMethod(Context ctx) returns any {
    string status = "ok";
    return {
        "status": status,
        "message": "no output directive"
    };
}
