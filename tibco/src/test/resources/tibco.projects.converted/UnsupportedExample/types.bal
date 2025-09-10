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

type XMLElementParseResult record {|
    string? namespace;
    string name;
|};

import ballerina/data.xmldata;

public type Foo record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

public type SequenceGroup record {|
    @xmldata:SequenceOrder {value: 1}
    string foo;
    @xmldata:SequenceOrder {value: 2}
    string bar?;
|};
