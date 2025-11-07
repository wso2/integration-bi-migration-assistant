public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}

function _dwMethod0_(Context ctx) returns json {
    var _var_0 = ["john", "peter", "matt"];
    return _var_0.'map(element => _var_0.indexOf(element).toString() + ":" + element.toUpperAscii());
}
