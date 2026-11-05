import ballerina/log;
import ballerina/uuid;

public type Context record {|
    anydata payload = ();
    string correlationId = uuid:createType4AsString();
|};

public function vmListenerFlow(Context ctx) {
    log:printInfo("Received " + ctx.correlationId.toString());
}
