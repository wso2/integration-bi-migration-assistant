import ballerina/log;

public type Context record {|
    anydata payload = ();
|};

public function demoFlow(Context ctx) {
    do {
        log:printInfo("xxx: main flow logger invoked");
    } on fail error e {
        // TODO: if conditions may require some manual adjustments
        if e.message() == "java.lang.NullPointerException" {
            log:printInfo("xxx: first catch condition invoked");
        } else if e.message() == "java.lang.IllegalArgumentException" || e.message() == "java.lang.IllegalStateException" {
            log:printInfo("xxx: second catch condition invoked");
        } else {
            log:printInfo("xxx: generic catch condition invoked");
        }
    }
}
