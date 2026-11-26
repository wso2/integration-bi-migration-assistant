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

public function _dwMethod(Context ctx) returns json|error => let json payload = check ctx.payload.cloneWithType() in (let json[] _var_1 = check payload.cloneWithType() in _var_1.enumerate().map(
        _var_0 => let int index = _var_0[0], var item = _var_0[1] in (let json[] _var_5 = check (check item.kids).cloneWithType() in _var_5.enumerate().map(
                _var_4 => let int _var_2 = _var_4[0], var _var_3 = _var_4[1] in {
                        "outer": index,
                        "inner": _var_2
                    }
))
));
