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

configurable string file_configWorkingDir = "/tmp/in";
public listener file:Listener file_config = new (
    path = file_configWorkingDir,
    recursive = false
);

service on file_config {
    function init() returns error? {
    }

    remote function onCreate(file:FileEvent event) {
        Context ctx = {attributes: {}};
        if regex:matches(event.name, "a..*\\.(htm|html|pdf)") {
            documentListenerFlow(ctx);
        }
    }

    remote function onModify(file:FileEvent event) {
        Context ctx = {attributes: {}};
        if regex:matches(event.name, "a..*\\.(htm|html|pdf)") {
            documentListenerFlow(ctx);
        }
    }
}

public function documentListenerFlow(Context ctx) {
    log:printInfo("document received");
}
