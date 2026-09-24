import ballerina/http;
import ballerina/log;

function convert(Context ctx) returns error? {
    ctx.variables.strVar = "hello";
    ctx.variables.intVar = 42;
    ctx.variables.intFromString = check stringToInt("123");
    ctx.variables.floatFromString = check stringToFloat("2.5");
    ctx.variables.boolFromString = check stringToBoolean("true");
    ctx.variables.xmlFromString = check stringToXml("<a/>");
    ctx.variables.intFromFloat = floatToInt(2.5);
    ctx.variables.intFromBool = check booleanToInt(true);
    ctx.variables.stringFromAny = convertToString(ctx.variables.intVar);
    ctx.variables.intFromAny = check convertToInt(ctx.variables.strVar);
    ctx.variables.floatFromAny = check convertToFloat(ctx.variables.strVar);
    ctx.variables.boolFromAny = check convertToBoolean(ctx.variables.strVar);
    ctx.variables.xmlFromAny = check convertToXml(ctx.variables.strVar);
    ctx.variables.jsonFromAny = check convertToJson(ctx.variables.strVar);
    // TODO: unsupported Synapse scope in expression: '$query:q'
}

function stringToInt(string v) returns int|error {
    return int:fromString(v);
}

function stringToFloat(string v) returns float|error {
    return float:fromString(v);
}

function stringToBoolean(string v) returns boolean|error {
    return boolean:fromString(v);
}

function stringToXml(string v) returns xml|error {
    return xml:fromString(v);
}

function floatToInt(float v) returns int {
    return <int>v;
}

function booleanToInt(boolean v) returns int|error {
    return error("Cannot convert boolean to int.");
}

function convertToString(anydata v) returns string {
    return v.toString();
}

function convertToInt(anydata v) returns int|error {
    if v is int {
        return v;
    }
    if v is float {
        return <int>v;
    }
    if v is decimal {
        return <int>v;
    }
    if v is string {
        return int:fromString(v);
    }
    return error("Cannot convert the given value to int.");
}

function convertToFloat(anydata v) returns float|error {
    if v is float {
        return v;
    }
    if v is int {
        return <float>v;
    }
    if v is decimal {
        return <float>v;
    }
    if v is string {
        return float:fromString(v);
    }
    return error("Cannot convert the given value to float.");
}

function convertToBoolean(anydata v) returns boolean|error {
    if v is boolean {
        return v;
    }
    if v is string {
        return boolean:fromString(v);
    }
    return error("Cannot convert the given value to boolean.");
}

function convertToXml(anydata v) returns xml|error {
    if v is xml {
        return v;
    }
    if v is string {
        return xml:fromString(v);
    }
    return error("Cannot convert the given value to xml.");
}

function convertToJson(anydata v) returns json|error {
    if v is json|int|float|decimal|string|boolean {
        return v;
    }
    return error("Cannot convert the given value to json.");
}

function respond(Context ctx) returns error? {
    http:Caller? caller = ctx.caller;
    if caller is () {
        log:printError("Cannot send response: no reply transport available for this message");
        return error("Cannot send response: no reply transport available for this message");
    }
    http:Response response = new;
    response.setPayload(ctx.payload);
    foreach [string, string] [name, value] in ctx.headers.entries() {
        response.setHeader(name, value);
    }
    int? statusCode = ctx.statusCode;
    if statusCode is int {
        response.statusCode = statusCode;
    }
    check caller->respond(response);
}

function emitPayload(Context ctx, http:Request request) returns error? {
    string contentType = request.getContentType();
    if contentType.startsWith("application/json") {
        ctx.payload = check request.getJsonPayload();
    } else if contentType.startsWith("application/xml") || contentType.startsWith("text/xml") {
        ctx.payload = check request.getXmlPayload();
    } else if contentType.startsWith("text/") {
        ctx.payload = check request.getTextPayload();
    } else {
        ctx.payload = check request.getBinaryPayload();
    }
}
