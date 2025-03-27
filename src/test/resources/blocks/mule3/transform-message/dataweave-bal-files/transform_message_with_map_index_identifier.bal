function _dwMethod0_(json payload) returns json {
    var _var_0 = [1, 2, 3, 4];
    return _var_0.'map(element => element + _var_0.indexOf(element));
}

function sampleFlow() {
    json _dwOutput_ = _dwMethod0_(payload);
}
