import ballerina/data.xmldata;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
type XMLTransformExceptionType record {
    *XMLExceptionType;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type CreateActivityConfigClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean override;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean createDirectory;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean createMissingDirectories;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean overwrite;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
type ActivityTimedOutExceptionType record {
    *ActivityExceptionType;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
type FileAlreadyExistsExceptionType record {
    *FileExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string fileName;
};

type FileNotFoundException FileNotFoundExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type RemoveActivityOutputClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
type MissingByteCountExceptionType record {
    *XMLExceptionType;
};

type InputElement ();

type CorrelationValue string;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
type FileExceptionType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string msgCode;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
type ProcessContext record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string JobId;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string ApplicationName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string EngineName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string ProcessInstanceId;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string CustomJobId;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string[] TrackingInfo;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
type ErrorReport record {
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
    string MsgCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    anydata Data;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type ReadActivityConfigClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean excludeContent;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string encoding;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
type FileNotFoundExceptionType record {
    *FileExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string fileName;
};

type validateInput boolean;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
type IllegalCopyExceptionType record {
    *FileExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string fromFileName;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string toFileName;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type WriteActivityInputBinaryClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    int binaryContent;
};

type cdataSections string;

@xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
type LogMessageType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
    string level;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
    string formatter;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
    string message;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
    string msgCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
    string loggerName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.example.org/LogSchema"}
    string handler;
};

type renderDefaultPrefix string;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
type IllegalRenameExceptionType record {
    *FileExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string fromFileName;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string toFileName;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type fileInfoType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fullName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string location;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string configuredFileName;
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
};

type textEncoding string;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type EventSourceOuputNoContentClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string action;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    int timeOccurred;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type RenameActivityOutput record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
};

type CopyActivityConfig CopyActivityInputClass;

type ActivityException ActivityExceptionType;

type UnsupportedEncodingException UnsupportedEncodingExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
type FileIOExceptionType record {
    *FileExceptionType;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type fileContentTypeTextClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string textContent;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string encoding;
};

type xmlBytes int;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type EventSourceOuputBinaryClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string action;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string timeOccurred;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileContentTypeBinary fileContent;
};

type input WaitForFileChangeActivityInput;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
type FaultDetail record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string ActivityName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    anydata Data;
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
};

type XMLTransformException XMLTransformExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type files record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType[] fileInfo;
};

type IllegalRenameException IllegalRenameExceptionType;

type writeXsiTypes boolean;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type WriteActivityInputTextClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string textContent;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean addLineSeparator;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string encoding;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
type XMLRenderExceptionType record {
    *XMLExceptionType;
};

type ActivityTimedOutException complexTypeFault;

type byteEncoding string;

type FileAlreadyExistsException FileAlreadyExistsExceptionType;

type ValidationException ValidationExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
type DuplicateKeyExceptionType record {
    *ActivityExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string duplicateKey;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string previousJobID;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type CopyActivityInputClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fromFileName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string toFileName;
};

type xmlString string;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
type ValidationExceptionType record {
    *XMLExceptionType;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type ListFilesActivityInputClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName;
};

type ReadFileFaultData ReadFileFaultDataType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type ReadActivityOutputTextClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileContentTypeTextClass fileContent;
};

type MissingByteCountException MissingByteCountExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type ReadActivityInputClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName;
};

type result string;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type RenameActivityInputClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fromFileName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string toFileName;
};

type renderAsText boolean;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type fileContentTypeBinary record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    int binaryContent;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type ReadActivityOutputBinaryClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileContentTypeBinary fileContent;
};

type ActivityErrorDataType XMLRenderException|()|UnsupportedEncodingException|MissingByteCountException;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
type XMLParseExceptionType record {
    *XMLExceptionType;
};

type XMLRenderException XMLRenderExceptionType;

type RenameActivityConfig RenameActivityInputClass;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type ReadActivityOutputNoContentClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
type OptionalErrorReport record {
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
    string MsgCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    anydata Data;
};

type term ();

type FileIOException FileIOExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
type LogParametersType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
    string msgCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
    string loggerName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
    string logLevel;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
    string message;
};

type XMLParseException XMLParseExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type EventSourceConfigClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    int pollInterval;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean includeSubDirectories;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean includeCurrent;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean excludeContent;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string mode;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string encoding;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string encodingUsed;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string sortorder;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string sortby;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
type complexTypeFault record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/file/5.0/fileExceptions"}
    string msgCode;
};

//FIXME: Failed to convert type due to [ParseError] : Expected 1 children, but found: 0
//<complexType name="RemoveActivityConfigClass" xmlns="http://www.w3.org/2001/XMLSchema"/>

//<complexType name="RemoveActivityConfigClass" xmlns="http://www.w3.org/2001/XMLSchema"/>
type RemoveActivityConfigClass anydata;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type WaitForFileChangeActivityInput record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string key;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    int processTimeout;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
type ActivityExceptionType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string msgCode;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type CreateActivityInputClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type WriteActivityOutputClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type EventSourceOuputTextClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string action;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    int timeOccurred;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileContentTypeTextClass fileContent;
};

type DuplicateKeyException DuplicateKeyExceptionType;

type ListFilesActivityConfig ListFilesActivityInputClass;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
type UnsupportedEncodingExceptionType record {
    *XMLExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
    string encoding;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
type XMLExceptionType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/xmlExceptions"}
    string msgCode;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type CreateActivityOutputClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    fileInfoType fileInfo;
};

type XMLException XMLExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type RemoveActivityInputClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string fileName;
};

type ReadFileFaultDataType FileNotFoundException|UnsupportedEncodingException|FileIOException;

type FileException FileExceptionType;

type ActivityErrorData ActivityErrorDataType;

type LogMessage LogMessageType;

type IllegalCopyException IllegalCopyExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type WriteActivityConfigClass record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean createNewFile;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    boolean append;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string encoding;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    string compressFile;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
type ListFilesActivityOutput record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/file"}
    files files;
};

type ActivityInput LogParametersType;
