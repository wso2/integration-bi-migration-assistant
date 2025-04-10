public type result anydata;

public type LogMessage anydata;

public type LogParametersType anydata;

public type WriteActivityInputTextClass anydata;
 import ballerina/data.xmldata;

@xmldata:Namespace {uri: "http://www.example.org/LogResult"}
public type result record {|
string \#content;
|};

@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
public type LogMessageType record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
public type LogMessage record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
public type SequenceGroup record {|
@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
@xmldata:SequenceOrder {value: 1}
string level;
@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
@xmldata:SequenceOrder {value: 2}
string formatter?;
@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
@xmldata:SequenceOrder {value: 3}
string message;
@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
@xmldata:SequenceOrder {value: 4}
string msgCode;
@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
@xmldata:SequenceOrder {value: 5}
string loggerName?;
@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
@xmldata:SequenceOrder {value: 6}
string handler?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xml/render/example"}
public type InputElement record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xml/render/example"}
public type SequenceGroup1 record {|
@xmldata:Namespace {uri: "http://www.tibco.com/xml/render/example"}
@xmldata:SequenceOrder {value: 1}
string level;
@xmldata:Namespace {uri: "http://www.tibco.com/xml/render/example"}
@xmldata:SequenceOrder {value: 2}
string message;
@xmldata:Namespace {uri: "http://www.tibco.com/xml/render/example"}
@xmldata:SequenceOrder {value: 3}
string logger;
@xmldata:Namespace {uri: "http://www.tibco.com/xml/render/example"}
@xmldata:SequenceOrder {value: 4}
string timestamp;
|};
