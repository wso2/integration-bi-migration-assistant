import ballerina/http;
import ballerina/log;

function omInline(Context ctx) returns error? {
    ctx.variables.xxx = xml `<book xmlns="http://ws.apache.org/ns/synapse">
            <name>A Study in Scarlet</name>
            <author>
                <name>Arthur Conan Doyle</name>
            </author>
        </book>`;
    ctx.variables.yyy = xml `<book xmlns="http://ws.apache.org/ns/synapse">
            <name>The Sign of Four</name>
        </book>`;
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
