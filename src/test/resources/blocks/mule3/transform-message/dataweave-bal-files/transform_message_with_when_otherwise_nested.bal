function sampleFlow() {
    json _dwOutput_ = check _dwMethod0_(payload);
}

function _dwMethod0_(json payload) returns json|error {
    json _var_0;
    if check payload.country == "USA" {
        _var_0 = {"currency": "USD"};
    } else if check payload.country == "UK" {
        _var_0 = {"currency": "GBP"};
    } else {
        _var_0 = {"currency": "EUR"};
    }
    return _var_0;
}
