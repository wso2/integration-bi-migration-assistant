import ballerina/log;
import ballerina/task;
import ballerina/time;
import ballerina/xslt;

function LogTick(Context cx) returns error? {
    xml var0 = getFromContext(cx, "LogTick-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:tib="http://www.tibco.com/bw/xslt/custom-functions" version="2.0"><xsl:param name="Timer"/><xsl:template name="LogTick-input" match="/"><tns:ActivityInput><message><xsl:value-of select="$Timer/root/Hour"/></message></tns:ActivityInput></xsl:template></xsl:stylesheet>`);
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

function scopeFaultHandler(error err, Context cx) returns () {
    panic err;
}

function scopeScopeFn(Context cx) returns () {
    error? result = scopeActivityRunner(cx);
    if result is error {
        scopeFaultHandler(result, cx);
    }
}

function start_test_timer_ReadsOutput(Context params) returns () {
    scopeScopeFn(params);
}

function trigger_test_timer_ReadsOutput() returns () {
    map<SharedVariableContext> jobSharedVariables = {};
    map<xml> paramXML = {"$input": timerOutput()};
    Context cx = initContext(paramXML, jobSharedVariables);
    start_test_timer_ReadsOutput(cx);
}

listener TimerListener test_timer_ReadsOutput_timer = new ({interval: 3600, startTime: check timerStartTime("2024-01-01T08:00:00"), runOnce: true}, trigger_test_timer_ReadsOutput);

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
    private final function () returns () run;

    function init(function () returns () run) {
        self.run = run;
    }

    public function execute() {
        self.run();
    }
}

class TimerListener {
    private final TimerConfig|error config;
    private final function () returns () run;
    private task:JobId? jobId = ();

    // An error is a Timer the converter could not resolve; it fails the program at startup.
    function init(TimerConfig|error config, function () returns () run) {
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

// The fields of TIBCO's TimerOutputSchema in the machine's time zone, wrapped in the `root` that
// stands for the output element, so mappings such as $Timer/Hour resolve. Week is the week of
// the year counted from 1 January, and Time is assumed to be formatted as HH:mm:ss.
function timerOutput() returns xml {
    time:Utc now = time:utcNow();
    time:Zone? zone = timerSystemZone();
    time:Civil civil = zone is () ? time:utcToCivil(now) : zone.utcToCivil(now);
    int second = <int>(civil.second ?: 0d).floor();
    int[] daysBeforeMonth = [0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334];
    boolean leapYear = civil.year % 4 == 0 && (civil.year % 100 != 0 || civil.year % 400 == 0);
    int dayOfYear = daysBeforeMonth[civil.month - 1] + civil.day - 1
        + (leapYear && civil.month > 2 ? 1 : 0);
    int nowMillis = now[0] * 1000 + <int>(now[1] * 1000).floor();
    string date = string `${civil.year}-${timerPad(civil.month)}-${timerPad(civil.day)}`;
    string clock = string `${timerPad(civil.hour)}:${timerPad(civil.minute)}:${timerPad(second)}`;
    xml fields = xml `<Now>${nowMillis}</Now><Hour>${civil.hour}</Hour>`
        + xml `<Minute>${civil.minute}</Minute><Second>${second}</Second>`
        + xml `<Week>${dayOfYear / 7 + 1}</Week><Month>${civil.month}</Month>`
        + xml `<Year>${civil.year}</Year><Date>${date}</Date><Time>${clock}</Time>`
        + xml `<DayOfMonth>${civil.day}</DayOfMonth>`;
    return xml `<root>${fields}</root>`;
}

// Accepts epoch milliseconds, an ISO-8601 timestamp, or TIBCO's "yyyy MMM dd HH:mm:ss z"
// property format. TIBCO uses the machine's time zone when the start time has none.
function timerStartTime(string value) returns time:Utc?|error {
    string trimmed = value.trim();
    if trimmed == "" {
        return ();
    }
    int|error epochMillis = int:fromString(trimmed);
    if epochMillis is int {
        return [epochMillis / 1000, <decimal>(epochMillis % 1000) / 1000];
    }
    if re `\d{4}-.*`.isFullMatch(trimmed) {
        // RFC 3339 needs a "+hh:mm" offset, but TIBCO also writes "+hhmm".
        if re `.*[+-]\d{4}`.isFullMatch(trimmed) {
            int split = trimmed.length() - 2;
            return time:utcFromString(trimmed.substring(0, split) + ":"
                    + trimmed.substring(split));
        }
        if re `.*(Z|[+-]\d{2}:\d{2})`.isFullMatch(trimmed) {
            return time:utcFromString(trimmed);
        }
        time:Zone? systemZone = timerSystemZone();
        return systemZone is ()
            ? time:utcFromString(trimmed + "Z")
            : timerZoneToUtc(systemZone, check timerLocalCivil(trimmed));
    }
    string[] parts = re ` +`.split(trimmed);
    string[] months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
        "Dec"];
    int? month = parts.length() == 5 ? months.indexOf(parts[1]) : ();
    if month is () {
        return error("Unsupported timer start time: " + value);
    }
    string day = parts[2].length() == 1 ? "0" + parts[2] : parts[2];
    string localTime = string `${parts[0]}-${timerPad(month + 1)}-${day}T${parts[3]}`;
    string zone = parts[4];
    if zone == "UTC" || zone == "GMT" || zone == "Z" {
        return time:utcFromString(localTime + "Z");
    }
    if re `[+-]\d{2}:?\d{2}`.isFullMatch(zone) {
        return timerStartTime(localTime + zone);
    }
    // getZone panics on an unknown zone ID instead of returning nil.
    time:Zone?|error namedZone = trap time:getZone(zone);
    if namedZone !is time:Zone {
        return error("Unknown time zone in timer start time: " + value);
    }
    return timerZoneToUtc(namedZone, check timerLocalCivil(localTime));
}

// A zone converts only a civil time that carries an offset, so the zone's offset at that moment
// (which accounts for daylight saving) is looked up first.
function timerZoneToUtc(time:Zone zone, time:Civil local) returns time:Utc|error {
    time:Utc asUtc = check time:utcFromCivil({year: local.year, month: local.month, day: local.day,
        hour: local.hour, minute: local.minute, second: local.second, utcOffset: {hours: 0}});
    return time:utcFromCivil({year: local.year, month: local.month, day: local.day,
        hour: local.hour, minute: local.minute, second: local.second,
        utcOffset: zone.utcToCivil(asUtc).utcOffset ?: {hours: 0}});
}

function timerLocalCivil(string localTime) returns time:Civil|error {
    string[] dateAndTime = re `T`.split(localTime);
    string[] date = dateAndTime.length() == 2 ? re `-`.split(dateAndTime[0]) : [];
    string[] clock = dateAndTime.length() == 2 ? re `:`.split(dateAndTime[1]) : [];
    if date.length() != 3 || clock.length() < 2 {
        return error("Unsupported timer start time: " + localTime);
    }
    return {
        year: check int:fromString(date[0]),
        month: check int:fromString(date[1]),
        day: check int:fromString(date[2]),
        hour: check int:fromString(clock[0]),
        minute: check int:fromString(clock[1]),
        second: clock.length() > 2 ? check decimal:fromString(clock[2]) : 0
    };
}

// Some Ballerina runtimes fail to load the system zone; UTC is then used, which is also the
// usual zone of a container.
function timerSystemZone() returns time:Zone? {
    time:Zone|error zone = time:loadSystemZone();
    return zone is time:Zone ? zone : ();
}

function timerPad(int value) returns string {
    return value < 10 ? "0" + value.toString() : value.toString();
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
