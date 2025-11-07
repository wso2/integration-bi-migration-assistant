public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

function _dwMethod0_(Context ctx) returns json|error {
    json payload = check ctx.payload.ensureType(json);
    return {"name": check payload?.name ?: "Unknown"};
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod0_(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}
