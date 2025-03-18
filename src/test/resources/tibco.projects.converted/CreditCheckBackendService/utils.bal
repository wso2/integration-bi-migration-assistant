import ballerina/data.xmldata;

function convertToanydata(xml input) returns anydata {
    return checkpanic xmldata:parseAsType(input);
}

function toXML(map<anydata> data) returns xml {
    return checkpanic xmldata:toXml(data);
}

function convertToanydata(xml input) returns anydata {
    return checkpanic xmldata:parseAsType(input);
}
