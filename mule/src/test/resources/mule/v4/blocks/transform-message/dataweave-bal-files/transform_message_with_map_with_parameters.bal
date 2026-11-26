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

public function _dwMethod(Context ctx) returns json => ["john", "peter", "matt"].enumerate().map(
_var_0 => let int position = _var_0[0], var firstName = _var_0[1] in position.toString() + ":" + firstName.toUpperAscii()
);
