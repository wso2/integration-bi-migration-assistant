public type Context record {|
    anydata payload;
|};

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_();
}

function _dwMethod0_() returns json {
    return "apple".toUpperAscii();
}
