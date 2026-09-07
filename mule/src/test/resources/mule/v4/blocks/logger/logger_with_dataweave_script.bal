import ballerina/log;

public type Context record {|
    anydata payload = ();
|};

configurable string application_version = ?;
configurable string tracepoint_beforeRequest = ?;
configurable string tracepoint_afterRequest = ?;

public function _dwMethod1(Context ctx) returns json|error {
    string logPayload = "";
    string status = "ok";
    string msg = "After Request Send Email";
    return CustomLogMapper::logger({
                                      "correlationId": correlationId,
                                      "app": app,
                                      "mule": mule,
                                      "status": status,
                                      "message": msg,
                                      "version": application_version,
                                      "tracepoint": tracepoint_afterRequest,
                                      "businessProcess": check vars.businessProcess,
                                      ...(p("log.level") == "DEBUG" ? {"payload": logPayload} : {}),
                                      "env": p("mule.env")
                                  });
}

public function send\-email(Context ctx) {
    json logMessage0 = check _dwMethod(ctx);
    log:printInfo(logMessage0.toJsonString());

    // TODO: UNSUPPORTED MULE BLOCK ENCOUNTERED. MANUAL CONVERSION REQUIRED.
    // ------------------------------------------------------------------------
    // <outlook365:send-mail config-ref="Outlook365Config" xmlns:doc="http://www.mulesoft.org/schema/mule/documentation" doc:id="f0c4ddbe-d3e2-48b7-804a-7e5896a02733" doc:name="Send mail" userId="${outlook.userId}" xmlns:outlook365="http://www.mulesoft.org/schema/mule/outlook365"/>
    // ------------------------------------------------------------------------

    json logMessage1 = check _dwMethod1(ctx);
    log:printInfo(logMessage1.toJsonString());
}

public function _dwMethod(Context ctx) returns json|error {
    string logPayload = "";
    string status = "ok";
    string msg = "Before Request Send Email";
    return CustomLogMapper::logger({
                                      "correlationId": correlationId,
                                      "app": app,
                                      "mule": mule,
                                      "status": status,
                                      "message": msg,
                                      "version": application_version,
                                      "tracepoint": tracepoint_beforeRequest,
                                      "businessProcess": check vars.businessProcess,
                                      ...(p("log.level") == "DEBUG" ? {"payload": logPayload} : {}),
                                      "env": p("mule.env")
                                  });
}
