import ballerina/time;

type Context record {|
    anydata payload;
|};

function _dwMethod0_() returns json|error {
    return {"date": check time:civilFromString("2021-01-01").ensureType(json), "time": check time:civilFromString("23:59:56").ensureType(json), "timeZone": check time:civilFromString("-08:00").ensureType(json), "dateTime": check time:civilFromString("2003-10-01T23:57:59-03:00").ensureType(json), "localDateTime": check time:civilFromString("2003-10-01T23:57:59").ensureType(json)};
}

function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod0_();
}
