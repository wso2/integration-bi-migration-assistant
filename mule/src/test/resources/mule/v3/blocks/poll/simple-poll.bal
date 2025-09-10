import ballerina/lang.runtime;
import ballerina/log;
import ballerina/task;

public type Context record {|
    anydata payload = ();
|};

class PollJob {
    *task:Job;

    public function execute() {
        log:printInfo("xxx: polling triggered");
    }
}

public function main() returns error? {
    runtime:sleep(2.0);
    task:JobId id = check task:scheduleJobRecurByFrequency(new PollJob(), 5.0);
}
