function _dwMethod0_(json payload) returns json {
    var _var_0 = [1, 2, 3, 4];
    return {"hail1": _var_0.length()};
}

function sampleFlow() {
    json _dwOutput_ = _dwMethod0_(payload);
}
