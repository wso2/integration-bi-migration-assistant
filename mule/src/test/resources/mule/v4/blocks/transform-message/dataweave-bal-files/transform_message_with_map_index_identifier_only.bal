public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}

public function _dwMethod(Context ctx) returns json => [1, 2, 3, 4].enumerate().map(
_var_2 => let int _var_0 = _var_2[0], var _var_1 = _var_2[1] in _var_0 + 1
);
