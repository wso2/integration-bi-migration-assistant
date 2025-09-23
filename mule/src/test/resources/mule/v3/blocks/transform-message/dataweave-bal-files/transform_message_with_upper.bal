public type Context record {|
    anydata payload = ();
|};

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_();
    ctx.payload = _dwOutput_;
}

function _dwMethod0_(Context ctx) returns json {
    return "apple".toUpperAscii();
}
