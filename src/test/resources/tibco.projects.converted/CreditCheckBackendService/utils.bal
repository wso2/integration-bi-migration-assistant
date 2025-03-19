import ballerina/data.xmldata;
import ballerinax/jdbc;

const jdbc:Client jdbcProperty = checkpanic new ("jdbcProperty");

function convertToQueryData0(xml input) returns QueryData0 {
    return checkpanic xmldata:parseAsType(input);
}

function convertToQueryData1(xml input) returns QueryData1 {
    return checkpanic xmldata:parseAsType(input);
}

function convertToanydata(xml input) returns anydata {
    return checkpanic xmldata:parseAsType(input);
}

function convertToanydata(xml input) returns anydata {
    return checkpanic xmldata:parseAsType(input);
}

function toXML(map<anydata> data) returns xml {
    return checkpanic xmldata:toXml(data);
}
