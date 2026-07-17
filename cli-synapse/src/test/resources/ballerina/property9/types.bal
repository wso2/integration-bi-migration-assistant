public type Variables record {|
    string synProp?;
    int defProp?;
|};

public type Context record {|
    Variables variables;
|};
