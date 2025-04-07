type Context record {|
    anydata payload;
|};

function sampleFlow(Context ctx) {
    string _dwOutput_ = _dwMethod0_();
}

function _dwMethod0_() returns string {
    return "Hello World";
}
