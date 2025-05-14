import ballerina/http;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;

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

mysql:Client MySQL_Configuration = check new ("localhost", "root", "admin123", "test_db", 3306);
public listener http:Listener config = new (8081);

service /mule3 on config {
    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};

        // database operation
        sql:ParameterizedQuery dbQuery0 = `SELECT * FROM users;`;
        stream<Record, sql:Error?> dbStream0 = MySQL_Configuration->query(dbQuery0);
        Record[] dbSelect0 = check from Record _iterator_ in dbStream0
            select _iterator_;
        ctx.payload = dbSelect0;

        // json transformation
        json to_json0 = dbSelect0.toJson();
        ctx.payload = to_json0;

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
