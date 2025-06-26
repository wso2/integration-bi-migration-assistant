import ballerina/time;

public type Context record {|
    anydata payload = ();
|};

function _dwMethod0_() returns json|error {
    time:Utc _utcValue_ = check time:utcFromCivil(check time:civilFromString("2005-06-02T15:10:16Z"));
    return {"mydate1": check (check time:utcFromCivil(check time:civilFromString("2005-06-02T15:10:16Z")))[0].ensureType(json), "mydate2": check (_utcValue_[0] * 1000 + <int>(_utcValue_[1] * 1000)).ensureType(json), "mydate3": check (check time:utcFromCivil(check time:civilFromString("2005-06-02T15:10:16Z")))[0].ensureType(json)};
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod0_();
    ctx.payload = _dwOutput_;
}
