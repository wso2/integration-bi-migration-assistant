public type Context record {|
    anydata payload = ();
|};

function _dwMethod0_(Context ctx) returns json {
    return {"name": "Ballerina " + "Conversion"};
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_();
    ctx.payload = _dwOutput_;
}
