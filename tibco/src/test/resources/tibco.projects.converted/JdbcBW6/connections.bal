import ballerina/http;
import ballerinax/java.jdbc;

http:Client test_api_MainProcess_client = checkpanic new ("localhost:8080/TestAPI/test");
jdbc:Client dbConnection = checkpanic new ("dbURL", "username", "password");
