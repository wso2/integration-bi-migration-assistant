import ballerina/http;
import ballerina/log;

public type Vars record {|
    string var1?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public listener http:Listener Listener = new (9091);

public function raise\-error\-example\-flow(Context ctx) {
    log:printInfo("flow starting logger invoked.");
    ctx.vars.var1 = "initial value";
    ctx.vars.var1 = "updated value";
    log:printInfo("flow ending logger invoked.");
}
