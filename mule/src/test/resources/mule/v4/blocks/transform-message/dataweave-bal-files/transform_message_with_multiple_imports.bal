public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function sampleFlow(Context ctx) returns error? {
    json _dwOutput_ = check _dwMethod(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}

public function _dwMethod(Context ctx) returns json|error {
    //TODO: UNSUPPORTED DATAWEAVE EXPRESSION 'importconfig,mapRecordfromdw::common::utils' FOUND. MANUAL CONVERSION REQUIRED.
    json payload = check ctx.payload.cloneWithType();
    return {"id": check payload.id};
}
