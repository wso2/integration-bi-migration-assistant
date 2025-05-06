import ballerina/http;

listener http:Listener GeneralConnection_sharedhttp = checkpanic new (9090, {host: "localhost"});

function addToContext(map<xml> context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
}
