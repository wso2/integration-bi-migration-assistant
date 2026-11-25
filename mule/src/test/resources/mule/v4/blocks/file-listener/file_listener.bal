import ballerina/file;
import ballerina/log;

public type Attributes record {|
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

configurable string file_configWorkingDir = "./foo";
public listener file:Listener file_config = new (
    path = file_configWorkingDir,
    recursive = false
);

// TODO: autoDelete attribute not supported: true
// TODO: outputMimeType attribute not supported: application/csv; separator=","
// TODO: directory attribute not supported: csv
service on file_config {
    function init() returns error? {
    }

    remote function onCreate(file:FileEvent event) returns error? {
        Context ctx = {attributes: {}};
        check fileListnerFlow(ctx);
    }

    remote function onModify(file:FileEvent event) returns error? {
        Context ctx = {attributes: {}};
        check fileListnerFlow(ctx);
    }
}

public function fileListnerFlow(Context ctx) returns error? {
    log:printInfo("xxx: logger invoked");
}
