public type Context record {|
    anydata payload;
|};

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_();
    ctx.payload = _dwOutput_;
}

function _dwMethod0_() returns json {
    var _var_0 = ["john", "peter", "matt"];
    return _var_0.'map(element => _var_0.indexOf(element).toString() + ":" + element.toUpperAscii());
}
