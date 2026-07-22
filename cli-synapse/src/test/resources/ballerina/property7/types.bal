import ballerina/http;

public type Variables record {|
    int|string shared?;
|};

public type Context record {|
    Variables variables;
    anydata payload = ();
    map<string> headers = {};
    int statusCode?;
    http:Caller caller?;
|};
