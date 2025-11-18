import ballerina/file;
import ballerina/log;
import ballerina/regex;

public type Attributes record {|
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

configurable string file_configWorkingDir = ?;
public listener file:Listener file_config = new (
    path = file_configWorkingDir,
    recursive = false
);

// TODO: scheduling-strategy not supported (frequency: ${lookupInterval}, timeUnit: SECONDS)
// TODO: autoDelete attribute not supported: true
// TODO: outputMimeType attribute not supported: application/csv; separator=","
// TODO: directory attribute not supported: csv
service on file_config {
    function init() returns error? {
    }

    remote function onCreate(file:FileEvent event) {
        Context ctx = {attributes: {}};
        if regex:matches(event.name, ".*\.csv") {
            fileListnerFlow(ctx);
        }
    }

    remote function onModify(file:FileEvent event) {
        Context ctx = {attributes: {}};
        if regex:matches(event.name, ".*\.csv") {
            fileListnerFlow(ctx);
        }
    }
}

public function fileListnerFlow(Context ctx) {
    log:printInfo("xxx: logger invoked");
}
