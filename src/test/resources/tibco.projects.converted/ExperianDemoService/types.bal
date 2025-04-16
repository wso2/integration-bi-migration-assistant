import ballerina/data.xmldata;

public type QueryData0 record {|
    string ssn;
|};

@xmldata:Name {
    value: "Record"
}
public type QueryResult0 record {|
    string firstname;
    string lastname;
    string ssn;
    string dateofBirth;
    int ficoscore;
    string rating;
    int numofpulls;
|};
