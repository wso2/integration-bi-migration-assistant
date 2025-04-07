type Context record {|
    anydata payload;
|};

function _dwMethod0_() returns string {
    return 10.toString();
}

function sampleFlow(Context ctx) {
    string _dwOutput_ = _dwMethod0_();
}
