import ballerina/http;
import ballerina/log;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;

public type Vars record {|
    anydata dbConnectionString?;
|};

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
    Attributes attributes;
|};

public type Record record {
};

configurable string http_host = ?;
configurable string http_port = ?;
configurable string user_firstName = ?;
configurable string user_lastName = ?;
configurable string user_balance = ?;
configurable string db_host = ?;
configurable string db_user = ?;
configurable string db_password = ?;
configurable string db_database = ?;
configurable string db_port = ?;
mysql:Client db_config = check new (db_host, db_user, db_password, db_database, check int:fromString(db_port));
public listener http:Listener listener_config = new (check int:fromString(http_port));

service /mule4 on listener_config {
    resource function get property_access(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        ctx.vars.dbConnectionString = http_host + ":" + http_port;
        log:printInfo("App running on port: " + http_port);

        // database operation
        sql:ParameterizedQuery dbQuery0 = `SELECT * FROM USERS;`;
        stream<Record, sql:Error?> dbStream0 = db_config->query(dbQuery0);
        Record[] dbSelect0 = check from Record _iterator_ in dbStream0
            select _iterator_;
        ctx.payload = dbSelect0;
        log:printInfo("Welcome, " + user_firstName + " " + user_lastName + ". Your account balance is " + user_balance);

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
