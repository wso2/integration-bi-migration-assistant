public type Context record {|
    anydata payload = ();
|};

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod0_(ctx);
    ctx.payload = _dwOutput_;
}

function _dwMethod0_(Context ctx) returns json|error {
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
