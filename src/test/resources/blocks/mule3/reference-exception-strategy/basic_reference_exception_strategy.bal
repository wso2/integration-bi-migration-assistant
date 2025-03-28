import ballerina/log;

type Context record {|
    anydata payload;
|};

function catch\-exception\-strategy(error e) {
    log:printInfo("xxx: inside catch exception strategy");
}

function muleProject(Context ctx) {
    do {
        log:printInfo("xxx: end of flow reached");
    } on fail error e {
        catch\-exception\-strategy(e);
    }
}
