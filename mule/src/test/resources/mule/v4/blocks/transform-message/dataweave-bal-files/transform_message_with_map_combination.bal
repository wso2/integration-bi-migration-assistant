public type Context record {|
    anydata payload = ();
|};

function _dwMethod0_(Context ctx) returns json {
    var _var_0 = ["john", "peter", "matt"];
    return {"users": _var_0.'map(element => element.toUpperAscii())};
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_(ctx);
    ctx.payload = _dwOutput_;
}
