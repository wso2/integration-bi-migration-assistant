import ballerina/log;

public type Context record {|
    anydata payload = ();
|};

configurable string company_name = "WSO2";

public function demoFlow(Context ctx) {
    log:printInfo(string `Company name : ${company_name}`);
}
