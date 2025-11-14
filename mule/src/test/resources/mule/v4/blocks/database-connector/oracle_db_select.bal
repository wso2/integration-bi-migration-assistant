import ballerina/http;
import ballerina/sql;
import ballerinax/oracledb;
import ballerinax/oracledb.driver as _;

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

oracledb:Client oracle_config = check new ("localhost", "root", "admin123", "test_db", 1522);
oracledb:Client oracle_config2 = check new ("localhost", "admin", "nimda", "service_name", 6063);
public listener http:Listener config = new (8081);

service /mule4 on config {
    resource function get db(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};

        // database operation
        sql:ParameterizedQuery dbQuery0 = `SELECT * FROM users;`;
        stream<Record, sql:Error?> dbStream0 = oracle_config->query(dbQuery0);
        Record[] dbSelect0 = check from Record _iterator_ in dbStream0
            select _iterator_;
        ctx.payload = dbSelect0;

        // database operation
        sql:ParameterizedQuery dbQuery1 = `SELECT * FROM persons;`;
        stream<Record, sql:Error?> dbStream1 = oracle_config2->query(dbQuery1);
        Record[] dbSelect1 = check from Record _iterator_ in dbStream1
            select _iterator_;
        ctx.payload = dbSelect1;

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
