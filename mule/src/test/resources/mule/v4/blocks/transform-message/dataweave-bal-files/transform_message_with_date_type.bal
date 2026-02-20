import ballerina/time;

public type Vars record {|
    json _dwOutput_?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
|};

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod(ctx);
    ctx.vars._dwOutput_ = _dwOutput_;
    ctx.payload = _dwOutput_;
}

public function _dwMethod(Context ctx) returns json|error => {
    "date": check time:civilFromString("2021-01-01"),
    "time": check time:civilFromString("23:59:56"),
    "timeZone": check time:civilFromString("-08:00"),
    "dateTime": check time:civilFromString("2003-10-01T23:57:59-03:00"),
    "localDateTime": check time:civilFromString("2003-10-01T23:57:59")
};
