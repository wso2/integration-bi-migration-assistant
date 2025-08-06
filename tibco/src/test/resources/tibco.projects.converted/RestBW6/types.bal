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
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type TestResponseType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type TestRequest record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type TestResponse record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup1 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    string request;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
public type SequenceGroup2 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/test/api"}
    @xmldata:SequenceOrder {value: 1}
    string response;
|};
