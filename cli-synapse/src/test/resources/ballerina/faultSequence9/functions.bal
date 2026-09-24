import ballerina/http;
import ballerina/log;

function convertToXml(anydata v) returns xml|error {
    if v is xml {
        return v;
    }
    if v is string {
        return xml:fromString(v);
    }
    return error("Cannot convert the given value to xml.");
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
