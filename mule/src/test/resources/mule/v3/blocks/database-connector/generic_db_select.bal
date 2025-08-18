import ballerina/http;
import ballerina/log;
import ballerina/sql;
import ballerinax/java.jdbc;

public type InboundProperties record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    InboundProperties inboundProperties;
|};

public type Record record {
};

jdbc:Client Generic_Database_Configuration = check new ("jdbc:postgresql://localhost:5432/bookstore", "root", "admin");
public listener http:Listener config = new (8081, {host: "localhost"});

service /mule3 on config {
    resource function get demo(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        log:printInfo("xxx: logger invoked");

        // database operation
        sql:ParameterizedQuery dbQuery0 = `SELECT * FROM users;`;
        stream<Record, sql:Error?> dbStream0 = Generic_Database_Configuration->query(dbQuery0);
        Record[] dbSelect0 = check from Record _iterator_ in dbStream0
            select _iterator_;
        ctx.payload = dbSelect0;

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
