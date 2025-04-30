import ballerina/data.xmldata;
import ballerina/log;

configurable string fileDir = ?;

function convertToLogParametersType(xml input) returns LogParametersType {
    return checkpanic xmldata:parseAsType(input);
}

function convertToWriteActivityInputTextClass(xml input) returns WriteActivityInputTextClass {
    return checkpanic xmldata:parseAsType(input);
}

function convertToresult(xml input) returns result {
    return checkpanic xmldata:parseAsType(input);
}

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function addToContext(map<xml> context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
}

function logWrapper(LogParametersType input) {
    foreach var body in input {
        match (body) {
            {message: var m, logLevel: "info"} => {
                log:printInfo(m);
            }
            {message: var m, logLevel: "debug"} => {
                log:printDebug(m);
            }
            {message: var m, logLevel: "warn"} => {
                log:printWarn(m);
            }
            {message: var m, logLevel: "error"} => {
                log:printError(m);
            }
            {message: var m} => {
                log:printInfo(m);
            }
        }
    }
}
