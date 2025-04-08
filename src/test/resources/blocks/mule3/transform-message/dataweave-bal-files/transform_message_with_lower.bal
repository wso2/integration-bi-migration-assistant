public type Context record {|
    anydata payload;
|};

function _dwMethod0_() returns json {
    return "APPLE".toLowerAscii();
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_();
}
