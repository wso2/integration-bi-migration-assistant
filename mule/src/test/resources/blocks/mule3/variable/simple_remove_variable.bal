public type FlowVars record {|
    string foo?;
|};

public type Context record {|
    anydata payload = ();
    FlowVars flowVars = {};
|};

public function weatherServiceFlow(Context ctx) {
    ctx.flowVars.foo = "hello";
    ctx.flowVars.foo = ();
}
