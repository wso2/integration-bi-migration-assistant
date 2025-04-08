public type Context record {|
    anydata payload;
|};

function _dwMethod0_() returns json|error {
    any[] _var_0 = [0, 1, 2];
    var _var_1 = [3, 4, 5];
    return {"a": check _var_0.push(..._var_1).ensureType(json)};
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod0_();
}
