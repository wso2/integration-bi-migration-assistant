public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

configurable string secure_xref_orderFiltering_entityApiName = ?;
configurable string secure_xref_orderFiltering_domianNameOrder = ?;

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}

public function _dwMethod(Context ctx) returns json|error => let json payload = check ctx.payload.cloneWithType(), json[] array = check (check payload.ChangeLog.ModTypes.Order).cloneWithType() in (array.map(item => {
        "entity": secure_xref_orderFiltering_entityApiName,
        "value": ((secure_xref_orderFiltering_entityApiName.toString() + "|" + check vars.sourceApi.toString() + "|" + item.toString() + "|" + "" + "|").toLowerAscii()) ?: "",
        "domain": secure_xref_orderFiltering_domianNameOrder,
        "defaultValue": false
    })) ?: ([
        {
            "entity": secure_xref_orderFiltering_entityApiName,
            "value": "",
            "domain": secure_xref_orderFiltering_domianNameOrder,
            "defaultValue": false
        }
    ]);
