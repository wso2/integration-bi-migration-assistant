import ballerina/data.xmldata;

function toXML(map<anydata> data) returns xml {
    return checkpanic xmldata:toXml(data);
}

function convertToExperianResponseSchemaElement(xml input) returns ExperianResponseSchemaElement {
    return checkpanic xmldata:parseAsType(input);
}
