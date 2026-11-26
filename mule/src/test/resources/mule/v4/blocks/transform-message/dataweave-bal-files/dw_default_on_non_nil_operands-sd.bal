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

public function _dwMethod(Context ctx) returns json|error {
    json payload = check ctx.payload.cloneWithType();
    json[] _var_0 = check (check payload.items).ensureType();
    return {
        "filtered": (check _var_0.filter(element => element.active)),
        "joined": check (check payload.a.toString() + "|" + check payload.b).ensureType(json),
        "nested": ((check payload.c) ?: ""),
        "nullFallback": ((check payload.d) ?: ()) ?: "y"
    };
}
