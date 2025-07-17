public type Context record {|
    anydata payload = ();
|};

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod0_(ctx);
    ctx.payload = _dwOutput_;
}

function _dwMethod0_(Context ctx) returns json|error {
    var _var_0 = {"aa": "a"};
    return {"concat": check {_var_0, "cc": "c"}.ensureType(json)};
}
