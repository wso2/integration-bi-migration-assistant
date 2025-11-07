public type Vars record {|
    string _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function sampleFlow(Context ctx) {
    string _dwOutput_ = _dwMethod0_(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}

function _dwMethod0_(Context ctx) returns string {
    return "Hello World";
}
