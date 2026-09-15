import ballerina/data.xmldata;

public type Context record {|
    map<xml> variables;
    xml result;
    Response_1 response?;
    map<SharedVariableContext> sharedVariables;
|};

public type JSONResponse readonly & record {|
    *Response_1;
    "JSONResponse" kind = "JSONResponse";
    json payload;
|};

public type Response_1 record {|
    "JSONResponse"|"XMLResponse"|"TextResponse" kind;
    anydata payload;
    map<string> headers;
|};

public type SharedVariableContext record {|
    function () returns xml getter;
    function (xml value) setter;
|};

public type TextResponse readonly & record {|
    *Response_1;
    "TextResponse" kind = "TextResponse";
    string payload;
|};

public type XMLResponse readonly & record {|
    *Response_1;
    "XMLResponse" kind = "XMLResponse";
    xml payload;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 65535}
    SequenceGroup[] sequenceGroup;
|};

@xmldata:Name {value: "ProcessContext"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 65535}
    SequenceGroup[] sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type SequenceGroup record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string JobId;
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string ApplicationName;
    @xmldata:SequenceOrder {value: 3}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string EngineName;
    @xmldata:SequenceOrder {value: 4}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string ProcessInstanceId;
    @xmldata:SequenceOrder {value: 5}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string CustomJobId?;
    @xmldata:SequenceOrder {value: 6}
    @xmldata:Element {minOccurs: 0, maxOccurs: 65535}
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
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string FirstName;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string LastName;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 3}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
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
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    request request;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup3 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
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
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    Response Response;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup5 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string FirstName;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string LastName;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 3}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    int Age;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup6 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string firstName?;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string lastName?;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 3}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string dateOfBirth?;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 4}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    int age?;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 5}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    int score?;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type Record record {|
    @xmldata:Sequence {minOccurs: 0, maxOccurs: 1}
    SequenceGroup6 sequenceGroup6?;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup7 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 65535}
    Record[] Record?;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type resultSet record {|
    @xmldata:Sequence {minOccurs: 0, maxOccurs: 65535}
    SequenceGroup7[] sequenceGroup7?;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup8 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    resultSet resultSet;
|};
