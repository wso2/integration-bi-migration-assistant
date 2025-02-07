import ballerina/http;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;

type Record record {
};

mysql:Client MySQL_Configuration = check new ("localhost", "root", "admin123", "test_db", 3306);
listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /mule3 on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;

        // database operation
        sql:ParameterizedQuery _dbQuery0_ = `SELECT * FROM users;`;
        stream<Record, sql:Error?> _dbStream0_ = MySQL_Configuration->query(_dbQuery0_);
        Record[] _dbSelect0_ = check from Record _iterator_ in _dbStream0_
            select _iterator_;
        _response_.setPayload(_dbSelect0_.toString());

        // json transformation
        json _to_json0_ = _dbSelect0_.toJson();
        _response_.setPayload(_to_json0_);
        return _response_;
    }
}
