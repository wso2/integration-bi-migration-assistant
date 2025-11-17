public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

function _dwMethod_(Context ctx) returns json|error {
    return check int:fromString("10");
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod_(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}
