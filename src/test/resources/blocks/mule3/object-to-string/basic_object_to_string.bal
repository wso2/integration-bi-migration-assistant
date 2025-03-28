import ballerina/http;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;

type InboundProperties record {|
    http:Response response;
|};

type Context record {|
    anydata payload;
    InboundProperties inboundProperties;
|};

type Record record {
};

mysql:Client MySQL_Configuration = check new ("localhost", "root", "admin123", "test_db", 3306);
listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /mule3 on config {
    Context ctx;

    function init() {
        self.ctx = {payload: (), inboundProperties: {response: new}};
    }

    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_(self.ctx);
    }

    private function _invokeEndPoint0_(Context ctx) returns http:Response|error {

        // database operation
        sql:ParameterizedQuery _dbQuery0_ = `SELECT * FROM users;`;
        stream<Record, sql:Error?> _dbStream0_ = MySQL_Configuration->query(_dbQuery0_);
        Record[] _dbSelect0_ = check from Record _iterator_ in _dbStream0_
            select _iterator_;

        // string transformation
        string _to_string0_ = _dbSelect0_.toString();
        ctx.inboundProperties.response.setPayload(_to_string0_);
        return ctx.inboundProperties.response;
    }
}
