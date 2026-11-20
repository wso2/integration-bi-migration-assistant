import ballerina/file;
import ballerina/log;

public type Attributes record {|
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

configurable string file_configWorkingDir = "/data/inbound";
configurable string file_configWorkingDir_2 = "/data/archive";
public listener file:Listener file\-config = new (
    path = file_configWorkingDir,
    recursive = false
);
public listener file:Listener file_config = new (
    path = file_configWorkingDir_2,
    recursive = false
);

service on file\-config {
    function init() returns error? {
    }

    remote function onCreate(file:FileEvent event) {
        Context ctx = {attributes: {}};
        inboundFlow(ctx);
    }

    remote function onModify(file:FileEvent event) {
        Context ctx = {attributes: {}};
        inboundFlow(ctx);
    }
}

service on file_config {
    function init() returns error? {
    }

    remote function onCreate(file:FileEvent event) {
        Context ctx = {attributes: {}};
        archiveFlow(ctx);
    }

    remote function onModify(file:FileEvent event) {
        Context ctx = {attributes: {}};
        archiveFlow(ctx);
    }
}

public function inboundFlow(Context ctx) {
    log:printInfo("inbound file");
}

public function archiveFlow(Context ctx) {
    log:printInfo("archived file");
}
