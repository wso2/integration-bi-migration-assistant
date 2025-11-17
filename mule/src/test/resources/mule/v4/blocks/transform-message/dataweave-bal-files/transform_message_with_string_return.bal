public type Vars record {|
    string _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

function _dwMethod(Context ctx) returns string {
    return "Hello World";
}

public function sampleFlow(Context ctx) {
    string _dwOutput_ = _dwMethod(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}
