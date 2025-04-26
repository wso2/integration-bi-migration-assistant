import ballerina/data.jsondata;
import ballerina/data.xmldata;
import ballerinax/java.jdbc;

jdbc:Client experianservice_module_JDBCConnectionResource = checkpanic new ("jdbc:postgresql://localhost:5432/bookstore", "bwuser", "#!+ZBCsMf2u4acq8mLX/mPA52dceRkuczQ");

function convertToExperianResponseSchemaElement(xml input) returns ExperianResponseSchemaElement {
    return checkpanic xmldata:parseAsType(input);
}

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function tryBindToInputElement(xml|json input) returns InputElement|error {
    return input is xml ? xmldata:parseAsType(input) : jsondata:parseAsType(input);
}

function addToContext(map<xml> context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
}
