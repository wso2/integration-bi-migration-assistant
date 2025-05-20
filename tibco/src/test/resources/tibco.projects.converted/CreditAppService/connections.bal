import ballerina/http;

http:Client creditapp_module_ExperianScore_client = checkpanic new ("localhost:8080/creditscore");
http:Client creditapp_module_EquifaxScore_client = checkpanic new ("localhost:8081/y54cuadtcxtfstqs3rux2gfdaxppoqgc/creditscore");
http:Client creditapp_module_MainProcess_client = checkpanic new ("localhost:8082/CreditDetails/creditdetails");
http:Client creditapp_module_HttpClientResource1 = checkpanic new (string `${host}:7080`);
http:Client creditapp_module_HttpClientResource2 = checkpanic new (string `${host_2}:13080`);
http:Client httpClient0 = checkpanic new ("/");
