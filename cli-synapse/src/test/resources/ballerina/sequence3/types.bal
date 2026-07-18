public type Variables record {|
    string str?;
    int i?;
|};

public type Context record {|
    Variables variables;
    anydata payload = ();
|};
