public type Context record {|
    anydata payload = ();
|};

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod0_(ctx.payload.toJson());
    ctx.payload = _dwOutput_;
}

function _dwMethod0_(Context ctx) returns json|error {
    json _var_0;
    if check payload.country == "USA" {
        _var_0 = {"currency": "USD"};
    } else {
        _var_0 = {"currency": "EUR"};
    }
    return _var_0;
}
