import ballerina/http;
import ballerinax/java.jdbc;

http:Client proj_annon_var0 = checkpanic new ("localhost:9090");
jdbc:Client JDBCConnection = checkpanic new ("jdbc:h2:~/path/to/database");
