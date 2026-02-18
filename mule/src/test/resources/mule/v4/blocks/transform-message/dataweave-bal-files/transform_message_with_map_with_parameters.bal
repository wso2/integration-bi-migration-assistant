public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function _dwMethod(Context ctx) returns json => ["john", "peter", "matt"].map(element => ["john", "peter", "matt"].indexOf(firstName).toString() + ":" + firstName.toUpperAscii()).toJsonString();

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}
