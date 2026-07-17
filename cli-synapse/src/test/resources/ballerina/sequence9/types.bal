public type Variables record {|
    string before?;
    string after?;
|};

public type Context record {|
    Variables variables;
|};
