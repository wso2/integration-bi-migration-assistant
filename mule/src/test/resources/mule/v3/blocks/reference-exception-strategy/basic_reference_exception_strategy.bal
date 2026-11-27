import ballerina/log;

public type Context record {|
    anydata payload = ();
|};

public function muleProject(Context ctx) returns error? {
    do {
        log:printInfo("xxx: end of flow reached");
    } on fail error e {
        check catch\-exception\-strategy(ctx, e);
    }
}

public function catch\-exception\-strategy(Context ctx, error e) returns error? {
    log:printInfo("xxx: inside catch exception strategy");
}
