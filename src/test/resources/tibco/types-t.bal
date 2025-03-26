import ballerina/http;

const string client_404_RecordNotFound = "Record Not Found";
public listener http:Listener com_test_Test_listener = new (8080, {host: "localhost"});

service /y54cuadtcxtfstqs3rux2gfdaxppoqgc on com_test_Test_listener {
    resource function post creditscore(GiveNewSchemaNameHere input) returns SuccessSchema|http:NotFound|http:InternalServerError|client_404_RecordNotFound {
        return com_test_Test_start(input);
    }
}

service / on com_test_Test_listener {
    resource function post creditscore(GiveNewSchemaNameHere input) returns SuccessSchema|http:NotFound|http:InternalServerError {
        return com_test_Test_start(input);
    }
}
