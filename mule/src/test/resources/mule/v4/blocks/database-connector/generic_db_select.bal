import ballerina/http;
import ballerina/sql;
import ballerinax/java.jdbc;

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

public type Record record {
};

jdbc:Client mySql_Config = check new ("jdbc:postgresql://localhost:5432/bookstore", "root", "admin");
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

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
