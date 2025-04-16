import ballerina/data.xmldata;
import ballerina/log;
import ballerinax/java.jdbc;

jdbc:Client creditcheckservice_JDBCConnectionResource = checkpanic new (string `${dbURL} jdbc:postgresql://awagle:5432/bookstore`, "bwuser", "#!yk2zPUfipGX2vB+1XNJha9KX6eLVDmcZ");
configurable string dbURL = ?;

function convertToElement(xml input) returns Element {
    return checkpanic xmldata:parseAsType(input);
}

function convertToLogParametersType(xml input) returns LogParametersType {
    return checkpanic xmldata:parseAsType(input);
}

function convertToQueryData0(xml input) returns QueryData0 {
    return checkpanic xmldata:parseAsType(input);
}

function convertToQueryData1(xml input) returns QueryData1 {
    return checkpanic xmldata:parseAsType(input);
}

function convertToResponse(xml input) returns Response {
    return checkpanic xmldata:parseAsType(input);
}

function convertToResponse(xml input) returns Response {
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
    match (input) {
        {message: var m, logLevel: "info"} => {
            log:printInfo(m.toString());
        }
        {message: var m, logLevel: "debug"} => {
            log:printDebug(m.toString());
        }
        {message: var m, logLevel: "warn"} => {
            log:printWarn(m.toString());
        }
        {message: var m, logLevel: "error"} => {
            log:printError(m.toString());
        }
        {message: var m} => {
            log:printInfo(m.toString());
        }
    }
}

function transformXSLT(xml input) returns xml {
    xmlns "http://www.w3.org/1999/XSL/Transform" as xsl;
    xml<xml:Element> values = input/**/<xsl:value\-of>;
    foreach xml:Element item in values {
        map<string> attributes = item.getAttributes();
        string selectPath = attributes.get("select");
        int? index = selectPath.indexOf("/");
        string path;
        if index == () {
            path = selectPath;
        } else {
            path = selectPath.substring(0, index) + "/root" + selectPath.substring(index);
        }
        attributes["select"] = path;
    }
    xml<xml:Element> test = input/**/<xsl:'if>;
    foreach xml:Element item in test {
        map<string> attributes = item.getAttributes();
        string selectPath = attributes.get("test");
        int? index = selectPath.indexOf("/");
        string path;
        if index == () {
            path = selectPath;
        } else {
            path = selectPath.substring(0, index) + "/root" + selectPath.substring(index);
        }
        attributes["test"] = path;
    }
    return input;
}

function test(xml input, string xpath) returns boolean {
    // TODO: support XPath
    return false;
}
