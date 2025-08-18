import ballerina/log;
import ballerina/task;

public type Context record {|
    anydata payload = ();
|};

class Job {
    *task:Job;

    public function execute() {
        log:printInfo("Scheduler triggered]");
    }
}

public function main() returns error? {
    task:JobId id = check task:scheduleJobRecurByFrequency(new Job(), 5.0);
}
