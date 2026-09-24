import ballerina/http;
import ballerina/file;
import ballerina/io;
import ballerina/log;

function FileErrorSequence() returns error? {
    // TODO: Unsupported Synapse mediator '<log>' (from FileErrorSequence.xml). Mediator not supported; manual conversion required.
    // Original Synapse:
    // <log category="ERROR" xmlns="http://ws.apache.org/ns/synapse">
    //         <message>File processing failed: ${properties.synapse.ERROR_MESSAGE}</message>
    //     </log>
}

function FileProcessSequence() returns error? {
    // TODO: Unsupported Synapse mediator '<log>' (from FileProcessSequence.xml). Mediator not supported; manual conversion required.
    // Original Synapse:
    // <log category="INFO" logFullPayload="true" xmlns="http://ws.apache.org/ns/synapse">
    //         <message>File received and processing complete</message>
    //     </log>

    // TODO: Unsupported Synapse mediator '<drop>' (from FileProcessSequence.xml). Mediator not supported; manual conversion required.
    // Original Synapse:
    // <drop xmlns="http://ws.apache.org/ns/synapse"/>
}

function fileInboundEndpointProcessFile(string path) returns error? {
    Context ctx = {variables: {}};
    do {
        ctx.payload = check io:fileReadString(path);
        check FileProcessSequence();
        check file:rename(path, check file:joinPath(fileInboundEndpointMoveAfterProcessPath, check file:basename(path)));
    } on fail error err {
        ctx.variables.ERROR_MESSAGE = err.message();
        check FileErrorSequence();
    }
}

function fileInboundEndpointScanExistingFiles() {
    do {
        file:MetaData[] & readonly fileInboundEndpointExistingFiles = check file:readDir(fileInboundEndpointPath);
        foreach file:MetaData m in fileInboundEndpointExistingFiles {
            if !m.dir {
                check fileInboundEndpointProcessFile(m.absPath);
            }
        }
    } on fail error err {
        log:printError("Failed to process pre-existing files for inbound endpoint 'FileInboundEndpoint'", 'error = err);
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
    _ = start fileInboundEndpointScanExistingFiles();
}
