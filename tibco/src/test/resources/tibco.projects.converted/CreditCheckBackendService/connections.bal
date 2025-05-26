import ballerina/http;
import ballerinax/java.jdbc;

http:Client creditcheckservice_Process_client = checkpanic new ("localhost:8080/CreditScore/creditscore");
jdbc:Client creditcheckservice_JDBCConnectionResource = checkpanic new (string `${dbURL} jdbc:postgresql://awagle:5432/bookstore`, "bwuser", "#!yk2zPUfipGX2vB+1XNJha9KX6eLVDmcZ");
