public type Context record {|
    anydata payload = ();
|};

function _dwMethod0_(json payload) returns json|error {
    return {"hail1": check payload.resultSet1.ensureType(json)};
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod0_(ctx.payload.toJson());
    ctx.payload = _dwOutput_;
}
