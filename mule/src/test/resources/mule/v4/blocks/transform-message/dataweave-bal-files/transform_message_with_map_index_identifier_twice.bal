public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}

public function _dwMethod(Context ctx) returns json|error => let json payload = check ctx.payload.cloneWithType() in {
        "a": (let json[] _var_3 = check (check payload.x).cloneWithType() in _var_3.enumerate().map(
            _var_2 => let int _var_0 = _var_2[0], var _var_1 = _var_2[1] in _var_0
)),
        "b": (let json[] _var_7 = check (check payload.y).cloneWithType() in _var_7.enumerate().map(
            _var_6 => let int _var_4 = _var_6[0], var _var_5 = _var_6[1] in _var_4
))
    };
