import ballerina/data.xmldata;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLTransformExceptionType record {|
    *XMLExceptionType;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityConfigClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean override?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean createDirectory?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean createMissingDirectories?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean overwrite?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityTimedOutExceptionType record {|
    *ActivityExceptionType;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileAlreadyExistsExceptionType record {|
    *FileExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string fileName;
|};

public type FileNotFoundException FileNotFoundExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RemoveActivityOutputClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type MissingByteCountExceptionType record {|
    *XMLExceptionType;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/xml/render/example"}
public type InputElement record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/xml/render/example"}
    string level;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/xml/render/example"}
    string message;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/xml/render/example"}
    string logger;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/xml/render/example"}
    string timestamp;
|};

public type CorrelationValue string;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileExceptionType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string msgCode?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string JobId;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string ApplicationName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string EngineName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string ProcessInstanceId;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string CustomJobId?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string[] TrackingInfo;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
public type ErrorReport record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string StackTrace;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string Msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string FullClass;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string Class;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string ProcessStack;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string MsgCode?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    anydata Data?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityConfigClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean excludeContent?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string encoding?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileNotFoundExceptionType record {|
    *FileExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string fileName;
|};

public type validateInput boolean;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type IllegalCopyExceptionType record {|
    *FileExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string fromFileName;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string toFileName;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityInputBinaryClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    int binaryContent;
|};

public type cdataSections string;

@xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
public type LogMessageType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
    string level;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
    string formatter?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
    string message;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
    string msgCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
    string loggerName?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
    string handler?;
|};

public type renderDefaultPrefix string;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type IllegalRenameExceptionType record {|
    *FileExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string fromFileName;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string toFileName;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type fileInfoType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fullName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string location;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string configuredFileName?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string type;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean readProtected;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean writeProtected;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    int size;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string lastModified;
|};

public type textEncoding string;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputNoContentClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string action;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    int timeOccurred;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityOutput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
|};

public type CopyActivityConfig CopyActivityInputClass;

public type ActivityException ActivityExceptionType;

public type UnsupportedEncodingException UnsupportedEncodingExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type FileIOExceptionType record {|
    *FileExceptionType;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type fileContentTypeTextClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string textContent;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string encoding?;
|};

public type xmlBytes int;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputBinaryClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string action;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string timeOccurred;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileContentTypeBinary fileContent;
|};

public type input WaitForFileChangeActivityInput;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
public type FaultDetail record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string ActivityName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    anydata Data?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string Msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string MsgCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string ProcessStack;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string StackTrace;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string FullClass;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string Class;
|};

public type XMLTransformException XMLTransformExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type files record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType[] fileInfo;
|};

public type IllegalRenameException IllegalRenameExceptionType;

public type writeXsiTypes boolean;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityInputTextClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string textContent;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean addLineSeparator?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string encoding?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLRenderExceptionType record {|
    *XMLExceptionType;
|};

public type ActivityTimedOutException complexTypeFault;

public type byteEncoding string;

public type FileAlreadyExistsException FileAlreadyExistsExceptionType;

public type ValidationException ValidationExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type DuplicateKeyExceptionType record {|
    *ActivityExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string duplicateKey;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string previousJobID?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CopyActivityInputClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fromFileName?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string toFileName?;
|};

public type xmlString string;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type ValidationExceptionType record {|
    *XMLExceptionType;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityInputClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName?;
|};

public type ReadFileFaultData ReadFileFaultDataType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputTextClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileContentTypeTextClass fileContent;
|};

public type MissingByteCountException MissingByteCountExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityInputClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName?;
|};

public type result string;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RenameActivityInputClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fromFileName?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string toFileName?;
|};

public type renderAsText boolean;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type fileContentTypeBinary record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    int binaryContent;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputBinaryClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileContentTypeBinary fileContent;
|};

public type ActivityErrorDataType XMLRenderException|()|UnsupportedEncodingException|MissingByteCountException;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLParseExceptionType record {|
    *XMLExceptionType;
|};

public type XMLRenderException XMLRenderExceptionType;

public type RenameActivityConfig RenameActivityInputClass;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ReadActivityOutputNoContentClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
public type OptionalErrorReport record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string StackTrace?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string Msg?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string FullClass?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string Class?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string ProcessStack?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string MsgCode?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    anydata Data?;
|};

//FIXME: Failed to convert type due to [ParseError] : Unsupported complex type body tag: attribute
//<xsd:complexType xmlns:xsd="http://www.w3.org/2001/XMLSchema">
//    <xsd:attribute name="ref" type="xsd:IDREF"/>
//</xsd:complexType>

//<xsd:element name="term" xmlns:xsd="http://www.w3.org/2001/XMLSchema">
//    <xsd:complexType>
//        <xsd:attribute name="ref" type="xsd:IDREF"/>
//    </xsd:complexType>
//</xsd:element>
public type term anydata;

public type FileIOException FileIOExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
public type LogParametersType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
    string msgCode?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
    string loggerName?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
    string logLevel?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
    string message;
|};

public type XMLParseException XMLParseExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceConfigClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    int pollInterval;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean includeSubDirectories?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean includeCurrent?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean excludeContent?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string mode?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string encoding?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string encodingUsed?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string sortorder?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string sortby?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
public type complexTypeFault record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string msgCode?;
|};

//FIXME: Failed to convert type due to [ParseError] : Expected 1 children, but found: 0
//<complexType name="RemoveActivityConfigClass" xmlns="http://www.w3.org/2001/XMLSchema"/>

//<complexType name="RemoveActivityConfigClass" xmlns="http://www.w3.org/2001/XMLSchema"/>
public type RemoveActivityConfigClass anydata;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WaitForFileChangeActivityInput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string key?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    int processTimeout?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityExceptionType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string msgCode?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityInputClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityOutputClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type EventSourceOuputTextClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string action;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    int timeOccurred;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileContentTypeTextClass fileContent;
|};

public type DuplicateKeyException DuplicateKeyExceptionType;

public type ListFilesActivityConfig ListFilesActivityInputClass;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type UnsupportedEncodingExceptionType record {|
    *XMLExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
    string encoding;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
public type XMLExceptionType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
    string msgCode?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type CreateActivityOutputClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
|};

public type XMLException XMLExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type RemoveActivityInputClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName?;
|};

public type ReadFileFaultDataType FileNotFoundException|UnsupportedEncodingException|FileIOException;

public type FileException FileExceptionType;

public type ActivityErrorData ActivityErrorDataType;

public type LogMessage LogMessageType;

public type IllegalCopyException IllegalCopyExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type WriteActivityConfigClass record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean createNewFile?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean append?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string encoding?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string compressFile?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
public type ListFilesActivityOutput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    files files;
|};

public type ActivityInput LogParametersType;
