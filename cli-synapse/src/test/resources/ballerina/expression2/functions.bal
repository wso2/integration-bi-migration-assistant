import ballerina/http;
import ballerina/log;

function expr(Context ctx) returns error? {
    ctx.variables.message = "Hello World";
    ctx.variables.messageCopy = convertToString(ctx.variables.message);
    ctx.variables.untypedMessage = "Hello Untyped";
    ctx.variables.untypedMessageCopy = convertToString(ctx.variables.untypedMessage);
    ctx.variables.integerValue = 23;
    ctx.variables.integerValueCopy = check convertToInt(ctx.variables.integerValue);
    ctx.variables.integerExpression = 23;
    ctx.variables.integerExpressionCopy = check convertToInt(ctx.variables.integerExpression);
    ctx.variables.numericString = 23.toString();
    ctx.variables.numericStringCopy = convertToString(ctx.variables.numericString);
    ctx.variables.longFromInt = 23;
    ctx.variables.floatFromInt = 23;
    ctx.variables.doubleFromInt = 23;
    ctx.variables.floatFromIntExpression = 23;
    ctx.variables.floatValue = 2.5;
    ctx.variables.doubleFromFloat = 2.5;
    ctx.variables.stringFromFloat = 2.5.toString();
    ctx.variables.stringFromBoolean = true.toString();
    ctx.variables.stringFromFloatExpression = 2.5.toString();
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
