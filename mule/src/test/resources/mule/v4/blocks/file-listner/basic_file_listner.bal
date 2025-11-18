import ballerina/file;
import ballerina/log;

public type Attributes record {|
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

configurable string workingDir = ?;
// TODO: palceholder listener for file_config
listener file:Listener file_config = new ({
    path: workingDir
});

// TODO: placeholder file listener for file_config
service on file_config {
    remote function onCreate(file:FileEvent event) {
        // FIXME: implement filters
        Context ctx = {attributes: {}};
        fileListnerFlow(ctx);
    }

    remote function onDelete(file:FileEvent event) {
        // FIXME: implement filters
        Context ctx = {attributes: {}};
        fileListnerFlow(ctx);
    }

    remote function onModify(file:FileEvent event) {
        // FIXME: implement filters
        Context ctx = {attributes: {}};
        fileListnerFlow(ctx);
    }
}

public function fileListnerFlow(Context ctx) {
    log:printInfo("xxx: logger invoked");
}
