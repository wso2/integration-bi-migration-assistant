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

configurable string http_port = ?;
configurable string db_host = ?;
configurable string db_user = ?;
configurable string db_password = ?;
configurable string db_database = ?;
configurable string db_port = ?;
mysql:Client db_config = check new (db_host, db_user, db_password, db_database, check int:fromString(db_port));
public listener http:Listener listener_config = new (check int:fromString(http_port));

service /mule4 on listener_config {
    resource function get property_access(http:Request request) returns http:Response|error {
        Context ctx = {inboundProperties: {request, response: new}};
        ctx.flowVars.dbConnectionString = p("http.host") + +":" + +p("http.port");
        log:printInfo("App running on port: " + +p.toString() ("http.port"));

        // database operation
        sql:ParameterizedQuery dbQuery0 = `SELECT * FROM USERS;`;
        stream<Record, sql:Error?> dbStream0 = db_config->query(dbQuery0);
        Record[] dbSelect0 = check from Record _iterator_ in dbStream0
            select _iterator_;
        ctx.payload = dbSelect0;
        log:printInfo("Welcome, " + +p.toString() ("user.firstName") + +" " + +p.toString() ("user.lastName") + +". Your account balance is " + +p.toString() ("user.balance"));

        ctx.inboundProperties.response.setPayload(ctx.payload);
        return ctx.inboundProperties.response;
    }
}
