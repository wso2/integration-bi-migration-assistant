public type Context record {|
    anydata payload = ();
|};

function _dwMethod0_(Context ctx) returns json {
    var _var_0 = [1, 2, 3, 4];
    return _var_0.'map(element => _var_0.indexOf(element) + 1);
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_(ctx);
    ctx.payload = _dwOutput_;
}
