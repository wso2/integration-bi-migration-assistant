import ballerina/lang.runtime;
import ballerina/log;
import ballerina/task;

public type Context record {|
    anydata payload = ();
|};

class schedulerJob {
    *task:Job;

    public function execute() {
        log:printInfo("Scheduler triggered]");
    }
}

public function main() returns error? {
    runtime:sleep(1.0);
    task:JobId id = check task:scheduleJobRecurByFrequency(new schedulerJob(), 4.0);
}
