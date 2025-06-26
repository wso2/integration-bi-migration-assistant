import ballerina/http;
import ballerina/log;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;

public type FlowVars record {|
    anydata dbConnectionString?;
|};

public type InboundProperties record {|
    http:Request request;
    http:Response response;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    FlowVars flowVars = {};
    InboundProperties inboundProperties;
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
mysql:Client MySQL_Config = check new (db_host, db_user, db_password, db_database, check int:fromString(db_port));
public listener http:Listener HTTP_Config = new (check int:fromString(http_port));
public listener http:Listener Listener_Config = new (8081);

service / on Listener_Config {
    resource function get test(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        ctx.flowVars.dbConnectionString = http_host + ":" + http_port;
        log:printInfo(string `App running on port: ${http_port}`);

        // database operation
        sql:ParameterizedQuery dbQuery0 = ``;
        stream<Record, sql:Error?> dbStream0 = MySQL_Config->query(dbQuery0);
        Record[] dbSelect0 = check from Record _iterator_ in dbStream0
            select _iterator_;
        ctx.payload = dbSelect0;
        log:printInfo(string ` Welcome, ${user_firstName} ${user_lastName}. Your account balance is ${user_balance}`);

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
