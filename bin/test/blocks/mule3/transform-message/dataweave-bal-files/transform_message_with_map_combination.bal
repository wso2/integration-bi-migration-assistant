public type Context record {|
    anydata payload;
|};

function _dwMethod0_() returns json {
    var _var_0 = ["john", "peter", "matt"];
    return {"users": _var_0.'map(element => element.toUpperAscii())};
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_();
}
