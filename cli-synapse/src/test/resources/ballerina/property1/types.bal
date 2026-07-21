import ballerina/http;

public type Variables record {|
    string greeting?;
    int count?;
    boolean enabled?;
|};

public type Context record {|
    Variables variables;
    anydata payload = ();
    map<string> headers = {};
    int statusCode?;
    http:Caller caller?;
|};
