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

@xmldata:Namespace {uri: "http://example.com/xsd/orders"}
public type 'order record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://example.com/xsd/orders"}
public type SequenceGroup record {|
    @xmldata:Namespace {uri: "http://example.com/xsd/orders"}
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string orderId;
    @xmldata:Namespace {uri: "http://example.com/xsd/orders"}
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string customer;
    @xmldata:Namespace {uri: "http://example.com/xsd/orders"}
    @xmldata:SequenceOrder {value: 3}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string amount;
|};
