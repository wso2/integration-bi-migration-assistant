import ballerina/http;
import ballerina/log;

function JMSErrorSeq() returns error? {
    // TODO: Unsupported Synapse mediator '<log>' (from JMSErrorSeq.xml). Mediator not supported; manual conversion required.
    // Original Synapse:
    // <log category="ERROR" xmlns="http://ws.apache.org/ns/synapse">
    //         <message>JMS error: ${properties.synapse.ERROR_CODE} - ${properties.synapse.ERROR_MESSAGE}</message>
    //     </log>

    // TODO: Unsupported Synapse mediator '<drop>' (from JMSErrorSeq.xml). Mediator not supported; manual conversion required.
    // Original Synapse:
    // <drop xmlns="http://ws.apache.org/ns/synapse"/>
}

function JMSInjectingSeq() returns error? {
    // TODO: Unsupported Synapse mediator '<log>' (from JMSInjectingSeq.xml). Mediator not supported; manual conversion required.
    // Original Synapse:
    // <log category="INFO" logFullPayload="true" xmlns="http://ws.apache.org/ns/synapse">
    //         <message>JMS message received: ${payload}</message>
    //     </log>

    // TODO: Unsupported Synapse mediator '<drop>' (from JMSInjectingSeq.xml). Mediator not supported; manual conversion required.
    // Original Synapse:
    // <drop xmlns="http://ws.apache.org/ns/synapse"/>
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
