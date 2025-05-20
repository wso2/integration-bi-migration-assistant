import ballerina/http;

http:Client creditcheckservice_Process_client = checkpanic new ("localhost:8080/CreditScore/creditscore");
