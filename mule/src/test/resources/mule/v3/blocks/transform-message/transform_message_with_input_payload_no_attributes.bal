public type Context record {|
    anydata payload = ();
|};

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod0_();
    ctx.payload = _dwOutput_;
}

function _dwMethod0_() returns json|error {
    return {"name": check payload[name].ensureType(json)};
}
