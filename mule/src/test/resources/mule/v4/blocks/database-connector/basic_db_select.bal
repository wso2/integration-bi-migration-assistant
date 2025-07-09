import ballerina/http;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;

public type Attributes record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

public type Record record {
};

mysql:Client mySql_Config = check new ("localhost", "root", "admin123", "test_db", 3306);
public listener http:Listener config = new (8081);

service /mule4 on config {
    resource function get db(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};

        // database operation
        sql:ParameterizedQuery dbQuery0 = `SELECT * FROM users;`;
        stream<Record, sql:Error?> dbStream0 = mySql_Config->query(dbQuery0);
        Record[] dbSelect0 = check from Record _iterator_ in dbStream0
            select _iterator_;
        ctx.payload = dbSelect0;

        ctx.attributes.response.setPayload(ctx.payload);
        return ctx.attributes.response;
    }
}
