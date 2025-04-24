import ballerina/http;

const string client_404_RecordNotFound = "Record Not Found";
public listener http:Listener com_test_Test_listener = new (8080, {host: "localhost"});

service /y54cuadtcxtfstqs3rux2gfdaxppoqgc on com_test_Test_listener {
    resource function post creditscore(GiveNewSchemaNameHere input) returns SuccessSchema|http:NotFound|http:InternalServerError|client_404_RecordNotFound {
        xml inputValXml = checkpanic toXML(input);
        xml extractedBody = inputValXml/*;
        xml inputXml = xml `<item>
    ${extractedBody}
</item>`;
        xml inputXmlMap = xml `<root>${inputXml}</root>`;
        map<xml> paramXML = {post: inputXmlMap};
        return start_com_test_Test(input, paramXML);
    }
}

service / on com_test_Test_listener {
    resource function post creditscore(GiveNewSchemaNameHere input) returns SuccessSchema|http:NotFound|http:InternalServerError {
        xml inputValXml = checkpanic toXML(input);
        xml extractedBody = inputValXml/*;
        xml inputXml = xml `<item>
    ${extractedBody}
</item>`;
        xml inputXmlMap = xml `<root>${inputXml}</root>`;
        map<xml> paramXML = {post: inputXmlMap};
        return start_com_test_Test(input, paramXML);
    }
}
