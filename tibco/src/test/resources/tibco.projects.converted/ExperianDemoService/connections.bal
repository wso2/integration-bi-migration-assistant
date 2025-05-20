import ballerina/http;

http:Client experianservice_module_Process_client = checkpanic new ("localhost:8080/Creditscore/creditscore");
