public type Vars record {|
    json sourceApi?;
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

configurable string secure_xref_orderFiltering_entityApiName = ?;

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}

public function _dwMethod(Context ctx) returns json => (secure_xref_orderFiltering_entityApiName.toString() + "|" + ctx.vars?.sourceApi.toString() + "|" + "MyOrderType" + "|" + "" + "|").toLowerAscii();
