public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

configurable string secure_xref_example_someApiName = ?;
configurable string secure_xref_example_someDomainName = ?;

public function _dwMethod(Context ctx) returns json => {
    "someKey": secure_xref_example_someApiName,
    "anotherKey": secure_xref_example_someDomainName
}.toJsonString();

public function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}
