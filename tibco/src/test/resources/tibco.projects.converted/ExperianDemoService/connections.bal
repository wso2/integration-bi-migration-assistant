import ballerina/http;
import ballerinax/java.jdbc;

http:Client experianservice_module_Process_client = checkpanic new ("localhost:8080/Creditscore/creditscore");
jdbc:Client experianservice_module_JDBCConnectionResource = checkpanic new ("jdbc:postgresql://localhost:5432/bookstore", "bwuser", "#!+ZBCsMf2u4acq8mLX/mPA52dceRkuczQ");
