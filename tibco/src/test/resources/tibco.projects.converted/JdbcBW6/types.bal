public type Context record {|
    map<xml> variables;
    xml result;
    Response_21 response?;
    map<SharedVariableContext> sharedVariables;
|};

public type JSONResponse readonly & record {|
    *Response_21;
    "JSONResponse" kind = "JSONResponse";
    json payload;
|};

public type Response_21 record {|
    "JSONResponse"|"XMLResponse"|"TextResponse" kind;
    anydata payload;
    map<string> headers;
|};

public type SharedVariableContext record {|
    function () returns xml getter;
    function (xml value) setter;
|};

public type TextResponse readonly & record {|
    *Response_21;
    "TextResponse" kind = "TextResponse";
    string payload;
|};

public type XMLResponse readonly & record {|
    *Response_21;
    "XMLResponse" kind = "XMLResponse";
    xml payload;
|};

import ballerina/data.xmldata;

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Name {value: "ProcessContext"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type SequenceGroup record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 1}
    string JobId;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 2}
    string ApplicationName;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 3}
    string EngineName;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 4}
    string ProcessInstanceId;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 5}
    string CustomJobId?;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 6}
    string[] TrackingInfo?;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type TestRequestType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type TestResponseType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type StartEventType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type QueryRecordsType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup8 sequenceGroup8;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type TestRequest record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type TestResponse record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type StartEvent record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type QueryRecords record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup8 sequenceGroup8;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup1 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    string FirstName;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 2}
    string LastName;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 3}
    int Age;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type request record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup2 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    request request;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup3 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    int Score;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type Response record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup4 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    Response Response;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup5 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    string FirstName;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 2}
    string LastName;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 3}
    int Age;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup6 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    string firstName?;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 2}
    string lastName?;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 3}
    string dateOfBirth?;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 4}
    int age?;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 5}
    int score?;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type Record record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup6 sequenceGroup6;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup7 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    Record[] Record?;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type resultSet record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup8 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    resultSet resultSet;
|};
