import ballerina/log;
import ballerina/task;
import ballerina/time;
import ballerina/xslt;

function LogTick(Context cx) returns error? {
    xml var0 = getFromContext(cx, "LogTick-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:tib="http://www.tibco.com/bw/xslt/custom-functions" version="2.0"><xsl:template name="LogTick-input" match="/"><tns:ActivityInput><message><xsl:value-of select="'literal tick'"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
}

function Timer(Context cx) returns error? {
    addToContext(cx, "Timer", getFromContext(cx, "$input"));
}

function scopeActivityRunner(Context cx) returns error? {
    check Timer(cx);
    check LogTick(cx);
}

function scopeFaultHandler(error err, Context cx) returns error? {
    return err;
}

function scopeScopeFn(Context cx) returns error? {
    error? result = scopeActivityRunner(cx);
    if result is error {
        check scopeFaultHandler(result, cx);
    }
}

function start_test_timer_Literal(Context params) returns error? {
    return scopeScopeFn(params);
}

function trigger_test_timer_Literal() returns error? {
    map<SharedVariableContext> jobSharedVariables = {};
    map<xml> paramXML = {};
    Context cx = initContext(paramXML, jobSharedVariables);
    check start_test_timer_Literal(cx);
}

listener TimerListener test_timer_Literal_timer = new ({interval: 900, startTime: check time:utcFromString("2024-01-01T08:00:00Z"), runOnce: false}, trigger_test_timer_Literal);

// A TIBCO Timer starter fires its process on a schedule. `task:Listener` can't host it because
// it requires an `isolated` execute method and the generated process functions aren't isolated,
// so this listener schedules a `task:Job` instead; being a listener keeps the program alive.
type TimerConfig record {|
    decimal interval = 1;
    time:Utc? startTime = ();
    boolean runOnce = false;
|};

class ProcessTimerJob {
    *task:Job;
    private final function () returns error? run;

    function init(function () returns error? run) {
        self.run = run;
    }

    public function execute() {
        error? result = self.run();
        // A failed process instance must not stop the timer, so it is only logged.
        if result is error {
            log:printError("Timer-triggered process failed", result);
        }
    }
}

class TimerListener {
    private final TimerConfig|error config;
    private final function () returns error? run;
    private task:JobId? jobId = ();

    // An error is a Timer the converter could not resolve; it fails the program at startup.
    function init(TimerConfig|error config, function () returns error? run) {
        self.config = config;
        self.run = run;
    }

    public function attach(service object {} s, string[]|string? name = ()) returns error? {
    }

    public function detach(service object {} s) returns error? {
    }

    public function 'start() returns error? {
        TimerConfig config = check self.config;
        ProcessTimerJob job = new (self.run);
        // TIBCO keeps firing after a failed process instance.
        task:TaskPolicy taskPolicy = {errorPolicy: task:LOG_AND_CONTINUE};
        // TIBCO fires every second when the interval is shorter than one second.
        decimal interval = decimal:max(config.interval, 1);
        time:Utc? startTime = config.startTime;
        time:Utc? fireTime = startTime is ()
            ? ()
            : firstTimerFireTime(startTime, config.runOnce ? () : interval);
        if config.runOnce {
            self.jobId = fireTime is ()
                ? check task:scheduleJobRecurByFrequency(job, 1, maxCount = 1,
                        taskPolicy = taskPolicy)
                : check task:scheduleOneTimeJob(job, time:utcToCivil(fireTime));
            return;
        }
        self.jobId = fireTime is ()
            ? check task:scheduleJobRecurByFrequency(job, interval, taskPolicy = taskPolicy)
            : check task:scheduleJobRecurByFrequency(job, interval,
                    startTime = time:utcToCivil(fireTime), taskPolicy = taskPolicy);
    }

    public function gracefulStop() returns error? {
        task:JobId? jobId = self.jobId;
        if jobId is task:JobId {
            check task:unscheduleJob(jobId);
        }
    }

    public function immediateStop() returns error? {
        return self.gracefulStop();
    }
}

// A start time in the past anchors a recurring schedule: TIBCO projects set an old date to mean
// "at this time of day", so the first run is the next start + n * interval after now. The
// scheduler rejects a start time that has passed by the time the job is registered, so a fire
// time less than a second away runs immediately instead (returned as nil).
function firstTimerFireTime(time:Utc startTime, decimal? interval) returns time:Utc? {
    decimal elapsed = time:utcDiffSeconds(time:utcNow(), startTime);
    time:Utc fireTime = interval is () || elapsed < 0d
        ? startTime
        : time:utcAddSeconds(startTime, ((elapsed / interval).floor() + 1) * interval);
    return time:utcDiffSeconds(fireTime, time:utcNow()) < 1d ? () : fireTime;
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}

function getFromContext(Context context, string varName) returns xml {
    xml? value = context.variables[varName];
    if value == () {
        return xml `<root/>`;
    }
    return value;
}

function initContext(map<xml> initVariables = {},
        map<SharedVariableContext> jobSharedVariables = {})
            returns Context {
    map<SharedVariableContext> sharedVariables = {};

    foreach var key in jobSharedVariables.keys() {
        sharedVariables[key] = jobSharedVariables.get(key);
    }
    return {variables: initVariables, result: xml `<root/>`, sharedVariables};
}
