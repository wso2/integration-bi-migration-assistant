import ballerina/http;
import ballerina/log;
import ballerina/task;
import ballerina/uuid;

public type Context record {|
    anydata payload = ();
    string correlationId = uuid:createType4AsString();
|};

class Job {
    *task:Job;

    public function execute() {
        do {
            check fetchPostFlow(ctx);
        } on fail error err {
            log:printError("Scheduled job failed", err);
        }
    }
}

public function main() returns error? {
    task:JobId id = check task:scheduleJobRecurByFrequency(new Job(), 5.0);
}

public function fetchPostFlow(Context ctx) returns error? {

    // http client request
    http:Client http_request_config = check new ("jsonplaceholder.typicode.com:80");
    http:Response clientResult0 = check http_request_config->/posts/latest.get(headers = {"X-Correlation-ID": ctx.correlationId});
    ctx.payload = check clientResult0.getJsonPayload();
}
