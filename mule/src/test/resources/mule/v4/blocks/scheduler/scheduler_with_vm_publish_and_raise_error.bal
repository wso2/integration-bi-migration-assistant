import ballerina/log;
import ballerina/task;

public type Context record {|
    anydata payload = ();
|};

public type APP__JOB_FAILED distinct error;

class Job {
    *task:Job;

    public function execute() {
        worker W returns error? {
            // VM Listener
            anydata receivedPayload = <- function;
            ctx.payload = receivedPayload;
            check vmReceive0(ctx);
        }
        do {

            // set payload
            string payload0 = "Hello World!";
            ctx.payload = payload0;

            // VM Publish
            ctx.payload -> W;
            fail error APP__JOB_FAILED("Job failed");
        } on fail error err {
            log:printError("Scheduled job failed", err);
        }
    }
}

public function main() returns error? {
    task:JobId id = check task:scheduleJobRecurByFrequency(new Job(), 5.0);
}

public function vmReceive0(Context ctx) returns error? {
    log:printInfo(string `Received a message: ${ctx.payload.toString()}`);
}
