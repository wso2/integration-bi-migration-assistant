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
    json[] array = check (check payload.items).cloneWithType();
    any[] _var_1 = [{"id": 0}];
    var _var_2 = array.map(element => {"id": check element.id});
    return {
        "filtered": (check _var_0.filter(element => element.active)),
        "joined": check (check payload.a.toString() + "|" + check payload.b).ensureType(json),
        "nested": ((check payload.c) ?: ""),
        "nullFallback": ((check payload.d) ?: ()) ?: "y",
        "groupedNullFallback": ((check payload.e) ?: ()) ?: "x",
        "nilableFallback": ((check payload.f) ?: (check payload.g)) ?: "w",
        "mappedConcat": check _var_1.push(..._var_2).ensureType(json)
    };
}
