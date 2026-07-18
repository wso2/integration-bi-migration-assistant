public type Variables record {|
    string temp?;
|};

public type Context record {|
    Variables variables;
    anydata payload = ();
|};
