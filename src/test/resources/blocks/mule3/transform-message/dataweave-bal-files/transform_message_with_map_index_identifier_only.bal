type Context record {|
    anydata payload;
|};

function sampleFlow(Context ctx) {
    json _dwOutput_ = _dwMethod0_(ctx.payload.toJson());
}

function _dwMethod0_(json payload) returns json {
    var _var_0 = [1, 2, 3, 4];
    return _var_0.'map(element => _var_0.indexOf(element) + 1);
}
