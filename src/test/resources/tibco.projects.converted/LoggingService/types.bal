import ballerina/data.xmldata;

@xmldata:Namespace {uri: "http://www.example.org/LogResult"}
public type result record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xml/render/example"}
public type InputElement record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xml/render/example"}
public type SequenceGroup record {|
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

@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
public type LogMessageType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
public type LogMessage record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
public type SequenceGroup1 record {|
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

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ErrorReport record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type OptionalErrorReport record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type FaultDetail record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5;
|};

@xmldata:Name {value: "anydata"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type Anydata record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup6 sequenceGroup6;
|};

@xmldata:Name {value: "OptionalErrorReport"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type OptionalErrorReport1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Name {value: "ErrorReport"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ErrorReport1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Name {value: "FaultDetail"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type FaultDetail1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Name {value: "ProcessContext"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type CorrelationValue record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type SequenceGroup2 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 1}
    string StackTrace;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 2}
    string Msg;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 3}
    string FullClass;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 4}
    string Class;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 5}
    string ProcessStack;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 6}
    string MsgCode?;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 7}
    anydata Data?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type SequenceGroup3 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 1}
    string StackTrace?;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 2}
    string Msg?;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 3}
    string FullClass?;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 4}
    string Class?;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 5}
    string ProcessStack?;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 6}
    string MsgCode?;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 7}
    anydata Data?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type SequenceGroup4 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 1}
    string ActivityName;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 2}
    anydata Data?;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 3}
    string Msg;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 4}
    string MsgCode;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 5}
    string ProcessStack;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 6}
    string StackTrace;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 7}
    string FullClass;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 8}
    string Class;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type SequenceGroup5 record {|
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

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type SequenceGroup6 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 1}
    string \#content;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityTimedOutExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityTimedOutException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type DuplicateKeyExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup8 sequenceGroup8;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type DuplicateKeyException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup8 sequenceGroup8;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type SequenceGroup7 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 1}
    string msg;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 2}
    string msgCode?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type SequenceGroup8 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 1}
    string duplicateKey;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 2}
    string previousJobID?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
public type LogParametersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup9 sequenceGroup9;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
public type ActivityInput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup9 sequenceGroup9;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
public type SequenceGroup9 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
    @xmldata:SequenceOrder {value: 1}
    string msgCode?;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
    @xmldata:SequenceOrder {value: 2}
    string loggerName?;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
    @xmldata:SequenceOrder {value: 3}
    string logLevel?;
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
    @xmldata:SequenceOrder {value: 4}
    string message;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceConfigClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup10 sequenceGroup10;
|};

@xmldata:Name {value: "EventSourceConfigClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceConfigClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup10 sequenceGroup10;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityConfigClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup11 sequenceGroup11;
|};

@xmldata:Name {value: "ReadActivityConfigClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityConfigClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup11 sequenceGroup11;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityInputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup12 sequenceGroup12;
|};

@xmldata:Name {value: "ReadActivityInputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityInputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup12 sequenceGroup12;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type fileInfoType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup13 sequenceGroup13;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type fileContentTypeBinary record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup14 sequenceGroup14;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type fileContentTypeTextClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup15 sequenceGroup15;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputNoContentClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup16 sequenceGroup16;
|};

@xmldata:Name {value: "ReadActivityOutputNoContentClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputNoContentClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup16 sequenceGroup16;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputBinaryClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup17 sequenceGroup17;
|};

@xmldata:Name {value: "ReadActivityOutputBinaryClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputBinaryClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup17 sequenceGroup17;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputTextClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup18 sequenceGroup18;
|};

@xmldata:Name {value: "ReadActivityOutputTextClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputTextClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup18 sequenceGroup18;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type FileIOException record {|
|};

@xmldata:Name {value: "FileIOException"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type FileIOException1 record {|
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputNoContentClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup19 sequenceGroup19;
|};

@xmldata:Name {value: "EventSourceOuputNoContentClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputNoContentClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup19 sequenceGroup19;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputBinaryClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup20 sequenceGroup20;
|};

@xmldata:Name {value: "EventSourceOuputBinaryClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputBinaryClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup20 sequenceGroup20;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputTextClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup21 sequenceGroup21;
|};

@xmldata:Name {value: "EventSourceOuputTextClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputTextClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup21 sequenceGroup21;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityConfigClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup22 sequenceGroup22;
|};

@xmldata:Name {value: "WriteActivityConfigClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityConfigClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup22 sequenceGroup22;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityInputBinaryClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup23 sequenceGroup23;
|};

@xmldata:Name {value: "WriteActivityInputBinaryClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityInputBinaryClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup23 sequenceGroup23;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityInputTextClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup24 sequenceGroup24;
|};

@xmldata:Name {value: "WriteActivityInputTextClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityInputTextClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup24 sequenceGroup24;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityOutputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup25 sequenceGroup25;
|};

@xmldata:Name {value: "WriteActivityOutputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityOutputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup25 sequenceGroup25;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityConfigClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup26 sequenceGroup26;
|};

@xmldata:Name {value: "CreateActivityConfigClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityConfigClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup26 sequenceGroup26;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityInputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup27 sequenceGroup27;
|};

@xmldata:Name {value: "CreateActivityInputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityInputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup27 sequenceGroup27;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityOutputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup28 sequenceGroup28;
|};

@xmldata:Name {value: "CreateActivityOutputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityOutputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup28 sequenceGroup28;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RemoveActivityConfigClass record {|
|};

@xmldata:Name {value: "RemoveActivityConfigClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RemoveActivityConfigClass1 record {|
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RemoveActivityInputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup29 sequenceGroup29;
|};

@xmldata:Name {value: "RemoveActivityInputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RemoveActivityInputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup29 sequenceGroup29;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RemoveActivityOutputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup30 sequenceGroup30;
|};

@xmldata:Name {value: "RemoveActivityOutputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RemoveActivityOutputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup30 sequenceGroup30;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityConfig record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup32 sequenceGroup32;
|};

@xmldata:Name {value: "RenameActivityConfig"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityConfig1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup31 sequenceGroup31;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityInputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup32 sequenceGroup32;
|};

@xmldata:Name {value: "RenameActivityInputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityInputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup32 sequenceGroup32;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityOutput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup33 sequenceGroup33;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityConfig record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup34 sequenceGroup34;
|};

@xmldata:Name {value: "ListFilesActivityConfig"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityConfig1 record {|
    ListFilesActivityInputClass \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityInputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup35 sequenceGroup35;
|};

@xmldata:Name {value: "ListFilesActivityInputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityInputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup35 sequenceGroup35;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type files record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup36 sequenceGroup36;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityOutput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup37 sequenceGroup37;
|};

@xmldata:Name {value: "ListFilesActivityOutput"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityOutput1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup37 sequenceGroup37;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CopyActivityConfig record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup38 sequenceGroup38;
|};

@xmldata:Name {value: "CopyActivityConfig"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CopyActivityConfig1 record {|
    CopyActivityInputClass \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CopyActivityInputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup39 sequenceGroup39;
|};

@xmldata:Name {value: "CopyActivityInputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CopyActivityInputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup39 sequenceGroup39;
|};

@xmldata:Name {value: "RenameActivityOutput"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityOutput1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup33 sequenceGroup33;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type input record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup40 sequenceGroup40;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WaitForFileChangeActivityInput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup40 sequenceGroup40;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup10 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fileName;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    int pollInterval;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 3}
    boolean includeSubDirectories?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 4}
    boolean includeCurrent?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 5}
    boolean excludeContent?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 6}
    string mode?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 7}
    string encoding?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 8}
    string encodingUsed?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 9}
    string sortorder?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 10}
    string sortby?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup11 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    boolean excludeContent?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    string encoding?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup12 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fileName?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup13 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fullName;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    string fileName;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 3}
    string location;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 4}
    string configuredFileName?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 5}
    string 'type;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 6}
    boolean readProtected;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 7}
    boolean writeProtected;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 8}
    int size;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 9}
    string lastModified;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup14 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    byte[] binaryContent;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup15 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string textContent;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    string encoding?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup16 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup17 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType fileInfo;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    fileContentTypeBinary fileContent;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup18 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType fileInfo;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    fileContentTypeTextClass fileContent;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup19 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string action;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    int timeOccurred;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 3}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup20 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string action;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    string timeOccurred;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 3}
    fileInfoType fileInfo;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 4}
    fileContentTypeBinary fileContent;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup21 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string action;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    int timeOccurred;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 3}
    fileInfoType fileInfo;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 4}
    fileContentTypeTextClass fileContent;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup22 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    boolean createNewFile?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    boolean append?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 3}
    string encoding?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 4}
    string compressFile?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup23 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fileName?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    byte[] binaryContent;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup24 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fileName?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    string textContent;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 3}
    boolean addLineSeparator?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 4}
    string encoding?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup25 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup26 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    boolean override?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    boolean createDirectory?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 3}
    boolean createMissingDirectories?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 4}
    boolean overwrite?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup27 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fileName?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup28 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup29 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fileName?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup30 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup31 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    boolean overwrite?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    boolean createMissingDirectories?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup32 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fromFileName?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    string toFileName?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup33 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup34 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string mode?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup35 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fileName?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup36 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType[] fileInfo?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup37 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    files files;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup38 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fromFileName;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    string toFileName;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 3}
    boolean overwrite?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 4}
    boolean createMissingDirectories?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup39 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fromFileName?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    string toFileName?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup40 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string 'key?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    int processTimeout?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup41 sequenceGroup41;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup41 sequenceGroup41;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type complexTypeFault record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup42 sequenceGroup42;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileNotFoundExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup41 sequenceGroup41;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup43 sequenceGroup43;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileNotFoundException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup43 sequenceGroup43;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type UnsupportedEncodingExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup41 sequenceGroup41;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup44 sequenceGroup44;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type UnsupportedEncodingException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup44 sequenceGroup44;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileIOExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup41 sequenceGroup41;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type IllegalRenameExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup41 sequenceGroup41;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup45 sequenceGroup45;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type IllegalRenameException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup45 sequenceGroup45;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileAlreadyExistsExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup41 sequenceGroup41;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup46 sequenceGroup46;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileAlreadyExistsException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup46 sequenceGroup46;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type IllegalCopyExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup41 sequenceGroup41;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup47 sequenceGroup47;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type IllegalCopyException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup47 sequenceGroup47;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type ReadFileFaultDataType record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption choiceOption;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type ReadFileFaultData record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption choiceOption;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type SequenceGroup41 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string msg;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 2}
    string msgCode?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type SequenceGroup42 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string msg;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 2}
    string msgCode?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type SequenceGroup43 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string fileName;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type SequenceGroup44 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string encoding;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type SequenceGroup45 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string fromFileName;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 2}
    string toFileName;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type SequenceGroup46 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string fileName;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type SequenceGroup47 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string fromFileName;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 2}
    string toFileName;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type ChoiceOption record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    FileNotFoundException FileNotFoundException?;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    UnsupportedEncodingException UnsupportedEncodingException?;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    FileIOException FileIOException?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.file.write"}
public type ActivityErrorData record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption1 choiceOption1;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.file.write"}
public type ActivityErrorDataType record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption1 choiceOption1;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.file.write"}
public type ChoiceOption1 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.file.write"}
    FileNotFoundException FileNotFoundException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.file.write"}
    UnsupportedEncodingException UnsupportedEncodingException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.file.write"}
    FileIOException FileIOException?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type byteEncoding record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type cdataSections record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type renderAsText record {|
    boolean \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type renderDefaultPrefix record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type term record {|
    @xmldata:Attribute
    string ref;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type textEncoding record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type validateInput record {|
    boolean \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type writeXsiTypes record {|
    boolean \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type xmlBytes record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type xmlString record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup48 sequenceGroup48;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup48 sequenceGroup48;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLRenderExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup48 sequenceGroup48;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLRenderException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLParseExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup48 sequenceGroup48;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLParseException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLTransformExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup48 sequenceGroup48;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLTransformException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type MissingByteCountExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup48 sequenceGroup48;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type MissingByteCountException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type ValidationExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup48 sequenceGroup48;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type ValidationException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type SequenceGroup48 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string msg;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
    @xmldata:SequenceOrder {value: 2}
    string msgCode?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type SequenceGroup49 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string encoding;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.xml.renderxml"}
public type ChoiceOption2 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.xml.renderxml"}
    XMLRenderException XMLRenderException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.xml.renderxml"}
    UnsupportedEncodingException UnsupportedEncodingException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.xml.renderxml"}
    MissingByteCountException MissingByteCountException?;
|};
