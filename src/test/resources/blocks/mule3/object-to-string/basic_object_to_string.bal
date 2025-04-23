import ballerina/http;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;

public type InboundProperties record {|
    http:Response response;
    http:Request request;
    map<string> uriParams;
|};

public type Context record {|
    anydata payload;
    InboundProperties inboundProperties;
|};

public type Record record {
};

mysql:Client MySQL_Configuration = check new ("localhost", "root", "admin123", "test_db", 3306);
public listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /mule3 on config {
    Context ctx;

    function init() {
        self.ctx = {payload: (), inboundProperties: {response: new, request: new, uriParams: {}}};
    }

    resource function get .(http:Request request) returns http:Response|error {
        self.ctx.inboundProperties.request = request;
        return _invokeEndPoint0_(self.ctx);
    }
}

public function _invokeEndPoint0_(Context ctx) returns http:Response|error {

    // database operation
    sql:ParameterizedQuery _dbQuery0_ = `SELECT * FROM users;`;
    stream<Record, sql:Error?> _dbStream0_ = MySQL_Configuration->query(_dbQuery0_);
    Record[] _dbSelect0_ = check from Record _iterator_ in _dbStream0_
        select _iterator_;
    ctx.payload = _dbSelect0_;

    // string transformation
    string _to_string0_ = _dbSelect0_.toString();
    ctx.payload = _to_string0_;

    ctx.inboundProperties.response.setPayload(ctx.payload);
    return ctx.inboundProperties.response;
}
