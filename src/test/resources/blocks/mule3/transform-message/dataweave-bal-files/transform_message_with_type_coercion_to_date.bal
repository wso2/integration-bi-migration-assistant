import ballerina/jballerina.java;
import ballerina/time;

public function UTC() returns handle = @java:FieldGet {
    'class: "java.time.ZoneOffset",
    name: "UTC"
} external;

public function parseDateTime(handle date, handle formatter) returns handle = @java:Method {
    'class: "java.time.LocalDateTime",
    name: "parse",
    paramTypes: ["java.lang.CharSequence", "java.time.format.DateTimeFormatter"]
} external;

public function getDateFromFormattedString(string dateString, string format) returns time:Utc|error {
    handle localDateTime = parseDateTime(java:fromString(dateString), getDateTimeFormatter(java:fromString(format)));
    return check time:utcFromString(toInstant(localDateTime, UTC()).toString());
}

public function getDateTimeFormatter(handle format) returns handle = @java:Method {
    'class: "java.time.format.DateTimeFormatter",
    name: "ofPattern",
    paramTypes: ["java.lang.String"]
} external;

public function toInstant(handle localDateTime, handle zoneOffset) returns handle = @java:Method {
    'class: "java.time.LocalDateTime",
    paramTypes: ["java.time.ZoneOffset"]
} external;

function sampleFlow() {
    json _dwOutput_ = check _dwMethod0_();
}

function _dwMethod0_() returns json|error {
    return {"a": [1436287232, 0], "b": check getDateFromFormattedString("2015-10-07 16:40:32.000", "yyyy-MM-dd HH:mm:ss.SSS")};
}
