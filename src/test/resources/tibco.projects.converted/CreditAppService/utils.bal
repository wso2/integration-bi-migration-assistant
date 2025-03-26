import ballerina/data.xmldata;

function convertToCreditScoreSuccessSchema(xml input) returns CreditScoreSuccessSchema {
    return checkpanic xmldata:parseAsType(input);
}

function convertToExperianResponseSchemaElement(xml input) returns ExperianResponseSchemaElement {
    return checkpanic xmldata:parseAsType(input);
}

function convertToGiveNewSchemaNameHere(xml input) returns GiveNewSchemaNameHere {
    return checkpanic xmldata:parseAsType(input);
}

function convertToHTTPRequestConfig(xml input) returns HTTPRequestConfig {
    return checkpanic xmldata:parseAsType(input);
}

function convertToSuccessSchema(xml input) returns SuccessSchema {
    return checkpanic xmldata:parseAsType(input);
}

function fromJson(json data) returns error|xml {
    return xmldata:fromJson(data);
}

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function addToContext(map<xml> context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
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

function addToContext(map<xml> context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
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
