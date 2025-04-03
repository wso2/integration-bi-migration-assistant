type Context record {|
    anydata payload;
|};

function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_();
}

function _dwMethod0_() returns json {
    return "APPLE".toLowerAscii();
}
