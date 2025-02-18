import ballerina/http;
import ballerina/log;

string sessionVarExample = "Initial Value";
listener http:Listener HTTP_Config = new (8081, {host: "0.0.0.0"});

service /mule3 on HTTP_Config {
    resource function get session() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;
        log:printInfo(string `Session Variable (Initial): ${sessionVars.sessionVarExample}`);
        sessionVarExample = "Modified Value";
        log:printInfo(string `Session Variable (Modified): ${sessionVars.sessionVarExample}`);

        // set payload
        string _payload0_ = "{\"message\":\"Check logs for session variable values\"}";
        _response_.setPayload(_payload0_);
        return _response_;
    }
}
