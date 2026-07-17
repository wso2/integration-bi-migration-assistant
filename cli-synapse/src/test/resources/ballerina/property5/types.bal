public type Variables record {|
    string barProp1?;
    int barProp2?;
    string before?;
    string after?;
|};

public type Context record {|
    Variables variables;
|};
