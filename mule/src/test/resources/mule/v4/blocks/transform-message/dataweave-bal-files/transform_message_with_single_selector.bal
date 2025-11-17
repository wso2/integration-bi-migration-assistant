public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

function _dwMethod_(Context ctx) returns json|error {
    json payload = check ctx.payload.ensureType(json);
    return {"hail1": check payload.resultSet1};
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod_(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}
