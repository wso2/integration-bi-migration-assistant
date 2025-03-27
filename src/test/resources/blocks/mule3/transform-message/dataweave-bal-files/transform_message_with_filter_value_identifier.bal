function _dwMethod0_(json payload) returns json {
    var _var_0 = [1, 2, 3, 4];
    return _var_0.filter(element => element > 2);
}

function sampleFlow() {
    json _dwOutput_ = _dwMethod0_(payload);
}
