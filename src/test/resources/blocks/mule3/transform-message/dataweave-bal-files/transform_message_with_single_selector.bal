function sampleFlow() {
    json _dwOutput_ = check _dwMethod0_(payload);
}

function _dwMethod0_(json payload) returns json|error {
    return {"hail1": check payload.resultSet1.ensureType(json)};
}
