public type Context record {|
    anydata payload = ();
|};

function _dwMethod0_(Context ctx) returns string {
    return 10.toString();
}

public function sampleFlow(Context ctx) {
    string _dwOutput_ = _dwMethod0_();
    ctx.payload = _dwOutput_;
}
