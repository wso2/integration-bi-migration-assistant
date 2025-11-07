public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

function _dwMethod0_(Context ctx) returns json|error {
    any[] _var_0 = [0, 1, 2];
    var _var_1 = [3, 4, 5];
    return {"a": check _var_0.push(..._var_1).ensureType(json)};
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod0_(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}
