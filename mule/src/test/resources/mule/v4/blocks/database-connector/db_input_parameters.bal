import ballerina/http;
import ballerina/sql;
import ballerinax/mysql;
import ballerinax/mysql.driver as _;

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

mysql:Client mySql_Config = check new ("localhost", "root", "admin123", "test_db", 3306);
public listener http:Listener config = new (8081);

service /mule4 on config {
    function init() returns error? {
    }

    resource function get db(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};

        // database operation
        sql:ParameterizedQuery dbQuery0 = `SELECT * FROM users WHERE id = :id AND name = :name;`;
        stream<Record, sql:Error?> dbStream0 = mySql_Config->query(dbQuery0);

        // TODO: UNSUPPORTED MULE BLOCK ENCOUNTERED. MANUAL CONVERSION REQUIRED.
        // ------------------------------------------------------------------------
        // <db:input-parameters xmlns:db="http://www.mulesoft.org/schema/mule/db"><![CDATA[
        //         		#[{
        //             		'id': vars.userId,
        //             		'name': vars.userName
        //         		}]
        //     		]]></db:input-parameters>/n
        // ------------------------------------------------------------------------

        Record[] dbSelect0 = check from Record _iterator_ in dbStream0
            select _iterator_;
        ctx.payload = dbSelect0;

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
