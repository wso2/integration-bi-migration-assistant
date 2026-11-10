public type Context record {|
    anydata payload = ();
|};

public function sampleFlow(Context ctx) {
    json userName = check _dwMethod0_();
    json _dwOutput_ = check _dwMethod1_();
    ctx.payload = _dwOutput_;
}

function _dwMethod0_() returns json|error {
    return check payload.name;
}

function _dwMethod1_() returns json|error {
    return {"name": check payload.name.ensureType(json)};
}
