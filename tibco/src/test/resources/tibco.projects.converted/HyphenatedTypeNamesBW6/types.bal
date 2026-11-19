import ballerina/data.xmldata;

public type Context record {|
    map<xml> variables;
    xml result;
    Response response?;
    map<SharedVariableContext> sharedVariables;
|};

public type JSONResponse readonly & record {|
    *Response;
    "JSONResponse" kind = "JSONResponse";
    json payload;
|};

public type Response record {|
    "JSONResponse"|"XMLResponse"|"TextResponse" kind;
    anydata payload;
    map<string> headers;
|};

public type SharedVariableContext record {|
    function () returns xml getter;
    function (xml value) setter;
|};

public type TextResponse readonly & record {|
    *Response;
    "TextResponse" kind = "TextResponse";
    string payload;
|};

public type XMLResponse readonly & record {|
    *Response;
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
public type Test_RequestType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type Priority_Code_1 string;

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type Priority_Code int;

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type Priority_Level int|Priority_Code_1;

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type Test_Response record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type TestResponseType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Name {value: "Test-Request"}
@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type Test_Request record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type TestResponse record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Name {value: "Test-Response"}
@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type Test_Response1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup1 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string request;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    boolean lastSubset;
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 3}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    Priority_Level priority;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup2 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string status;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup3 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string response;
|};
