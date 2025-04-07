type Context record {|
    anydata payload;
|};

function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod0_();
}

function _dwMethod0_() returns json|error {
    return check int:fromString("10");
}
