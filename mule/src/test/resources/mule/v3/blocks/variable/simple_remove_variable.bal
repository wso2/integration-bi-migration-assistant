public type FlowVars record {|
    string greeting?;
    string 'from?;
|};

public type Context record {|
    anydata payload = ();
    FlowVars flowVars = {};
|};

public function weatherServiceFlow(Context ctx) {
    ctx.flowVars.greeting = "hello";
    ctx.flowVars.'from = "USA";
    ctx.flowVars.greeting = ();
    ctx.flowVars.'from = ();
}
