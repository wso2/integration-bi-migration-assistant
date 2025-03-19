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

function fromJson(json data) returns xml {
    return checkpanic xmldata:fromJson(data);
}

function toXML(map<anydata> data) returns xml {
    return checkpanic xmldata:toXml(data);
}
