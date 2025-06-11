import ballerina/http;

http:Client test_api_MainProcess_client = checkpanic new ("localhost:8080/TestAPI/test");
