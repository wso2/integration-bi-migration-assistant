public type Context record {|
    anydata payload;
|};

function _dwMethod0_(json payload) returns json {
    var _var_0 = [1, 2, 3, 4];
    return {"hail1": _var_0.length()};
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_(ctx.payload.toJson());
}
