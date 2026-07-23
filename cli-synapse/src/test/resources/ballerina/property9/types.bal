import ballerina/http;

public type Variables record {|
    string synProp?;
    int defProp?;
|};

public type Context record {|
    Variables variables;
    anydata payload = ();
    map<string> headers = {};
    int statusCode?;
    http:Caller caller?;
|};
