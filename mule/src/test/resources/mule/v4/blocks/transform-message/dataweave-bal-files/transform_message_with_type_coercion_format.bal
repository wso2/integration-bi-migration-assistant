import ballerina/jballerina.java;
import ballerina/time;

public type Context record {|
    anydata payload = ();
|};

public function getFormattedStringFromDate(string dateString, string format) returns string {
    handle localDateTime = getDateTime(parseInstant(java:fromString(dateString)),
            getZoneId(java:fromString("UTC")));
    return formatDateTime(localDateTime, getDateTimeFormatter(java:fromString(format))).toString();
}

public function sampleFlow(Context ctx) {
    json _dwOutput_ = check _dwMethod0_(ctx);
    ctx.payload = _dwOutput_;
}

public function parseInstant(handle instant) returns handle = @java:Method {
    'class: "java.time.Instant",
    name: "parse"
} external;

public function formatDateTime(handle dateTime, handle formatter) returns handle = @java:Method {
    'class: "java.time.LocalDateTime"
} external;

public function getDateTimeFormatter(handle format) returns handle = @java:Method {
    'class: "java.time.format.DateTimeFormatter",
    name: "ofPattern",
    paramTypes: ["java.lang.String"]
} external;

public function getDateTime(handle instant, handle zoneId) returns handle = @java:Method {
    'class: "java.time.LocalDateTime",
    name: "ofInstant",
    paramTypes: ["java.time.Instant", "java.time.ZoneId"]
} external;

public function getZoneId(handle zoneId) returns handle = @java:Method {
    'class: "java.time.ZoneId",
    name: "of",
    paramTypes: ["java.lang.String"]
} external;

function _dwMethod0_(Context ctx) returns json|error {
    return {"a": intToString(1, "##,#"), "b": check getFormattedStringFromDate(getCurrentTimeString(), "yyyy-MM-dd").ensureType(json), "c": true.toString()};
}

public function getFormattedStringFromNumber(handle formatObject, int value) returns handle = @java:Method {
    'class: "java.text.NumberFormat",
    name: "format",
    paramTypes: ["long"]
} external;

public function intToString(int intValue, string format) returns string {
    handle formatObj = newDecimalFormat(java:fromString(format));
    handle stringResult = getFormattedStringFromNumber(formatObj, intValue);
    return stringResult.toString();
}

public function newDecimalFormat(handle format) returns handle = @java:Constructor {
    'class: "java.text.DecimalFormat"
} external;

public function getCurrentTimeString() returns string {
    return time:utcToString(time:utcNow());
}
