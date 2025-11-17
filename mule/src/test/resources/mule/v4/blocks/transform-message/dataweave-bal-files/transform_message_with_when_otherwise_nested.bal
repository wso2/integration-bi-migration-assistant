public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

function _dwMethod_(Context ctx) returns json|error {
    json payload = check ctx.payload.ensureType(json);
    json _var_0;
    if check payload.country == "USA" {
        _var_0 = "USD";
    } else if check payload.country == "UK" {
        _var_0 = "GBP";
    } else {
        _var_0 = "EUR";
    }
    return {"currency": _var_0};
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod_(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}
