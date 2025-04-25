import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerina/http;

http:Client creditapp_module_HttpClientResource1 = checkpanic new (string `${host}:7080`);
http:Client creditapp_module_HttpClientResource2 = checkpanic new (string `${host_2}:13080`);
configurable string host = ?;
configurable string host_2 = ?;
http:Client httpClient0 = checkpanic new ("/");

function convertToCreditScoreSuccessSchema(xml input) returns CreditScoreSuccessSchema {
    return checkpanic xmldata:parseAsType(input);
}

function convertToExperianResponseSchemaElement(xml input) returns ExperianResponseSchemaElement {
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

function tryBindToGiveNewSchemaNameHere(xml|json input) returns GiveNewSchemaNameHere|error {
    return input is xml ? xmldata:parseAsType(input) : jsondata:parseAsType(input);
}

function addToContext(map<xml> context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
}
