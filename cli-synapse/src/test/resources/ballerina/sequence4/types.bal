public type Variables record {|
|};

public type Context record {|
    Variables variables;
    anydata payload = ();
|};
