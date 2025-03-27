function sampleFlow() {
    json _dwOutput_ = check _dwMethod0_();
}

function _dwMethod0_() returns json|error {
    var _var_0 = {"aa": "a"};
    return {"concat": check {_var_0, "cc": "c"}.ensureType(json)};
}
