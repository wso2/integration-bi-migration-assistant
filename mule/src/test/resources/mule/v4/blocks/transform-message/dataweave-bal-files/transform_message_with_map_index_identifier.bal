public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

function _dwMethod0_(Context ctx) returns json {
    var _var_0 = [1, 2, 3, 4];
    return _var_0.'map(element => element + _var_0.indexOf(element));
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}
