public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function _dwMethod(Context ctx) returns json {
    string:RegExp pattern = re `/(\d+)/`;
    return {"b": pattern.replace("admin123", "ID")};
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}
