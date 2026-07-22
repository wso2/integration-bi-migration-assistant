import ballerina/http;

public type Variables record {|
    string prop1?;
    int r1only?;
    boolean r2only?;
|};

public type Context record {|
    Variables variables;
    anydata payload = ();
    map<string> headers = {};
    int statusCode?;
    http:Caller caller?;
|};
