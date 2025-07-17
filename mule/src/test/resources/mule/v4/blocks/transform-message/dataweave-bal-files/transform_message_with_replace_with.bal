public type Context record {|
    anydata payload = ();
|};

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_(ctx);
    ctx.payload = _dwOutput_;
}

function _dwMethod0_(Context ctx) returns json {
    string:RegExp _pattern_ = re `/(\d+)/`;
    var _var_0 = "admin123";
    return {"b": _pattern_.replace(_var_0, "ID")};
}
