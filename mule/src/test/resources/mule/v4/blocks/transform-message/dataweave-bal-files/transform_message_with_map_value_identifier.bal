public type Context record {|
    anydata payload = ();
|};

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_(ctx);
    ctx.payload = _dwOutput_;
}

function _dwMethod0_(Context ctx) returns json {
    var _var_0 = [1, 2, 3, 4];
    return _var_0.'map(element => element + 1);
}
