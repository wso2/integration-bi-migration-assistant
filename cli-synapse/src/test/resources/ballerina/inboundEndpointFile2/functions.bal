import ballerina/http;
import ballerina/file;
import ballerina/io;
import ballerina/log;

function processArchive(Context ctx) returns error? {
    ctx.payload = {"status": "processed"};
}

function fileArchiveInboundProcessFile(string path) returns error? {
    Context ctx = {variables: {}};
    do {
        ctx.payload = check io:fileReadString(path);
        check processArchive(ctx);
        check file:remove(path);
    } on fail error err {
        log:printError("Unhandled error in mediation", 'error = err);
    }
}

function fileArchiveInboundScanExistingFiles() {
    do {
        file:MetaData[] & readonly fileArchiveInboundExistingFiles = check file:readDir(fileArchiveInboundPath);
        foreach file:MetaData m in fileArchiveInboundExistingFiles {
            if !m.dir {
                check fileArchiveInboundProcessFile(m.absPath);
            }
        }
    } on fail error err {
        log:printError("Failed to process pre-existing files for inbound endpoint 'FileArchiveInbound'", 'error = err);
    }
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

function init() returns error? {
    _ = start fileArchiveInboundScanExistingFiles();
}
