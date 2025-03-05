import ballerina/http;
import ballerina/time;

listener http:Listener config = new (8081, {host: "0.0.0.0"});

service /foo on config {
    resource function get .() returns http:Response|error {
        return self._invokeEndPoint0_();
    }

    private function _invokeEndPoint0_() returns http:Response|error {
        http:Response _response_ = new;
        json _dwOutput_ = check _dwMethod0_();
        _response_.setPayload(_dwOutput_);
        return _response_;
    }
}

function _dwMethod0_() returns json|error {
    return {"date": check time:civilFromString("2021-01-01").ensureType(json), "time": check time:civilFromString("23:59:56").ensureType(json), "timeZone": check time:civilFromString("-08:00").ensureType(json), "dateTime": check time:civilFromString("2003-10-01T23:57:59-03:00").ensureType(json), "localDateTime": check time:civilFromString("2003-10-01T23:57:59").ensureType(json)};
}
