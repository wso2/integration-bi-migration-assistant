@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type anydatarecord {|@xmldata:Sequence {minOccurs:1,maxOccurs:1}SequenceGroup4 sequenceGroup4;|};
@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.xml.renderxml"}
public type ActivityErrorData record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption choiceOption;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.xml.renderxml"}
public type ActivityErrorDataType record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption choiceOption;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
public type ActivityInput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type ActivityTimedOutException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityTimedOutExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.xml.renderxml"}
public type ChoiceOption record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.xml.renderxml"}
    XMLRenderException XMLRenderException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.xml.renderxml"}
    UnsupportedEncodingException UnsupportedEncodingException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.xml.renderxml"}
    MissingByteCountException MissingByteCountException?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CopyActivityConfig record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup28 sequenceGroup28;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CopyActivityConfig record {|
    CopyActivityInputClass \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CopyActivityInputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup29 sequenceGroup29;
|};

@xmldata:Name {value: "CopyActivityInputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CopyActivityInputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup29 sequenceGroup29;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type CorrelationValue record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityConfigClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup16 sequenceGroup16;
|};

@xmldata:Name {value: "CreateActivityConfigClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityConfigClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup16 sequenceGroup16;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityInputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup17 sequenceGroup17;
|};

@xmldata:Name {value: "CreateActivityInputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityInputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup17 sequenceGroup17;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityOutputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup18 sequenceGroup18;
|};

@xmldata:Name {value: "CreateActivityOutputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityOutputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup18 sequenceGroup18;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type DuplicateKeyException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type DuplicateKeyExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ErrorReport record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Name {value: "ErrorReport"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ErrorReport1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceConfigClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Name {value: "EventSourceConfigClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceConfigClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputBinaryClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup10 sequenceGroup10;
|};

@xmldata:Name {value: "EventSourceOuputBinaryClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputBinaryClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup10 sequenceGroup10;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputNoContentClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup9 sequenceGroup9;
|};

@xmldata:Name {value: "EventSourceOuputNoContentClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputNoContentClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup9 sequenceGroup9;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputTextClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup11 sequenceGroup11;
|};

@xmldata:Name {value: "EventSourceOuputTextClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputTextClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup11 sequenceGroup11;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type FaultDetail record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Name {value: "FaultDetail"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type FaultDetail1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileAlreadyExistsException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileAlreadyExistsExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileIOException record {|
|};

@xmldata:Name {value: "FileIOException"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type FileIOException1 record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileIOExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileNotFoundException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileNotFoundExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type IllegalCopyException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup6 sequenceGroup6;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type IllegalCopyExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup6 sequenceGroup6;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type IllegalRenameException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type IllegalRenameExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xml/render/example"}
public type InputElement record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityConfig record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup24 sequenceGroup24;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityConfig record {|
    ListFilesActivityInputClass \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityInputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup25 sequenceGroup25;
|};

@xmldata:Name {value: "ListFilesActivityInputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityInputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup25 sequenceGroup25;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityOutput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup27 sequenceGroup27;
|};

@xmldata:Name {value: "ListFilesActivityOutput"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityOutput1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup27 sequenceGroup27;
|};

@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
public type LogMessage record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.example.org/LogSchema"}
public type LogMessageType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
public type LogParametersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type MissingByteCountException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type MissingByteCountExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type OptionalErrorReport record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Name {value: "OptionalErrorReport"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type OptionalErrorReport1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Name {value: "ProcessContext"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityConfigClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Name {value: "ReadActivityConfigClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityConfigClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityInputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Name {value: "ReadActivityInputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityInputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputBinaryClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
|};

@xmldata:Name {value: "ReadActivityOutputBinaryClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputBinaryClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputNoContentClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup6 sequenceGroup6;
|};

@xmldata:Name {value: "ReadActivityOutputNoContentClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputNoContentClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup6 sequenceGroup6;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputTextClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup8 sequenceGroup8;
|};

@xmldata:Name {value: "ReadActivityOutputTextClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputTextClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup8 sequenceGroup8;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type ReadFileFaultData record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption choiceOption;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type ReadFileFaultDataType record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption choiceOption;
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
    SequenceGroup19 sequenceGroup19;
|};

@xmldata:Name {value: "RemoveActivityInputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RemoveActivityInputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup19 sequenceGroup19;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RemoveActivityOutputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup20 sequenceGroup20;
|};

@xmldata:Name {value: "RemoveActivityOutputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RemoveActivityOutputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup20 sequenceGroup20;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityConfig record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup22 sequenceGroup22;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityConfig record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup21 sequenceGroup21;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityInputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup22 sequenceGroup22;
|};

@xmldata:Name {value: "RenameActivityInputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityInputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup22 sequenceGroup22;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityOutput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup23 sequenceGroup23;
|};

@xmldata:Name {value: "RenameActivityOutput"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityOutput1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup23 sequenceGroup23;
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

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type SequenceGroup1 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string encoding;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup10 record {|
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
public type SequenceGroup11 record {|
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
public type SequenceGroup12 record {|
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
public type SequenceGroup13 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fileName?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    byte[] binaryContent;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup14 record {|
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
public type SequenceGroup15 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup16 record {|
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
public type SequenceGroup17 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fileName?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup18 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup19 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fileName?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type SequenceGroup2 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string fileName;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup20 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup21 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    boolean overwrite?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    boolean createMissingDirectories?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup22 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fromFileName?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    string toFileName?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup23 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup24 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string mode?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup25 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fileName?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup26 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType[] fileInfo?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup27 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    files files;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup28 record {|
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
public type SequenceGroup29 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string fromFileName?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    string toFileName?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type SequenceGroup3 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string encoding;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup30 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    string key?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    int processTimeout?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type SequenceGroup4 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string fromFileName;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 2}
    string toFileName;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type SequenceGroup5 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string fileName;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type SequenceGroup6 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string fromFileName;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    @xmldata:SequenceOrder {value: 2}
    string toFileName;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup7 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType fileInfo;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    fileContentTypeBinary fileContent;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup8 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 1}
    fileInfoType fileInfo;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    @xmldata:SequenceOrder {value: 2}
    fileContentTypeTextClass fileContent;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type SequenceGroup9 record {|
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

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type UnsupportedEncodingException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type UnsupportedEncodingExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type ValidationException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type ValidationExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WaitForFileChangeActivityInput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup30 sequenceGroup30;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityConfigClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup12 sequenceGroup12;
|};

@xmldata:Name {value: "WriteActivityConfigClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityConfigClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup12 sequenceGroup12;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityInputBinaryClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup13 sequenceGroup13;
|};

@xmldata:Name {value: "WriteActivityInputBinaryClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityInputBinaryClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup13 sequenceGroup13;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityInputTextClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup14 sequenceGroup14;
|};

@xmldata:Name {value: "WriteActivityInputTextClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityInputTextClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup14 sequenceGroup14;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityOutputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup15 sequenceGroup15;
|};

@xmldata:Name {value: "WriteActivityOutputClass"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityOutputClass1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup15 sequenceGroup15;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLParseException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLParseExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLRenderException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLRenderExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLTransformException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLTransformExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type byteEncoding record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type cdataSections record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type complexTypeFault record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type fileContentTypeBinary record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type fileContentTypeTextClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type fileInfoType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type files record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup26 sequenceGroup26;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type input record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup30 sequenceGroup30;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type renderAsText record {|
    boolean \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/renderxml"}
public type renderDefaultPrefix record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://www.example.org/LogResult"}
public type result record {|
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
