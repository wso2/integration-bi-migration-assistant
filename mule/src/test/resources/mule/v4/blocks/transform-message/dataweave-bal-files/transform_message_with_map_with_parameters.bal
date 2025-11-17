public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

function _dwMethod_(Context ctx) returns json {
    var _var_0 = ["john", "peter", "matt"];
    return _var_0.'map(element => _var_0.indexOf(element).toString() + ":" + element.toUpperAscii());
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod_(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}
