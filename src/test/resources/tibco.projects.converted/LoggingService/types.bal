type XMLTransformExceptionType record {
    *XMLExceptionType;
};

type CreateActivityConfigClass record {
    boolean override;
    boolean createDirectory;
    boolean createMissingDirectories;
    boolean overwrite;
};

type ActivityTimedOutExceptionType record {
    *ActivityExceptionType;
};

type FileAlreadyExistsExceptionType record {
    *FileExceptionType;
    string fileName;
};

type FileNotFoundException FileNotFoundExceptionType;

type RemoveActivityOutputClass record {
    fileInfoType fileInfo;
};

type MissingByteCountExceptionType record {
    *XMLExceptionType;
};

type InputElement ();

type CorrelationValue string;

type FileExceptionType record {
    string msg;
    string msgCode;
};

type ProcessContext record {
    string JobId;
    string ApplicationName;
    string EngineName;
    string ProcessInstanceId;
    string CustomJobId;
    string TrackingInfo;
};

type ErrorReport record {
    string StackTrace;
    string Msg;
    string FullClass;
    string Class;
    string ProcessStack;
    string MsgCode;
    anydata Data;
};

type ReadActivityConfigClass record {
    boolean excludeContent;
    string encoding;
};

type FileNotFoundExceptionType record {
    *FileExceptionType;
    string fileName;
};

type validateInput boolean;

type IllegalCopyExceptionType record {
    *FileExceptionType;
    string fromFileName;
    string toFileName;
};

type WriteActivityInputBinaryClass record {
    string fileName;
    int binaryContent;
};

type cdataSections string;

type LogMessageType record {
    string level;
    string formatter;
    string message;
    string msgCode;
    string loggerName;
    string handler;
};

type renderDefaultPrefix string;

type IllegalRenameExceptionType record {
    *FileExceptionType;
    string fromFileName;
    string toFileName;
};

type fileInfoType record {
    string fullName;
    string fileName;
    string location;
    string configuredFileName;
    string type;
    boolean readProtected;
    boolean writeProtected;
    int size;
    string lastModified;
};

type textEncoding string;

type EventSourceOuputNoContentClass record {
    string action;
    int timeOccurred;
    fileInfoType fileInfo;
};

type RenameActivityOutput record {
    fileInfoType fileInfo;
};

type CopyActivityConfig CopyActivityInputClass;

type ActivityException ActivityExceptionType;

type UnsupportedEncodingException UnsupportedEncodingExceptionType;

type FileIOExceptionType record {
    *FileExceptionType;
};

type fileContentTypeTextClass record {
    string textContent;
    string encoding;
};

type xmlBytes int;

type EventSourceOuputBinaryClass record {
    string action;
    string timeOccurred;
    fileInfoType fileInfo;
    fileContentTypeBinary fileContent;
};

type input WaitForFileChangeActivityInput;

type FaultDetail record {
    string ActivityName;
    anydata Data;
    string Msg;
    string MsgCode;
    string ProcessStack;
    string StackTrace;
    string FullClass;
    string Class;
};

type XMLTransformException XMLTransformExceptionType;

type files record {
    fileInfoType fileInfo;
};

type IllegalRenameException IllegalRenameExceptionType;

type writeXsiTypes boolean;

type WriteActivityInputTextClass record {
    string fileName;
    string textContent;
    boolean addLineSeparator;
    string encoding;
};

type XMLRenderExceptionType record {
    *XMLExceptionType;
};

type ActivityTimedOutException complexTypeFault;

type byteEncoding string;

type FileAlreadyExistsException FileAlreadyExistsExceptionType;

type ValidationException ValidationExceptionType;

type DuplicateKeyExceptionType record {
    *ActivityExceptionType;
    string duplicateKey;
    string previousJobID;
};

type CopyActivityInputClass record {
    string fromFileName;
    string toFileName;
};

type xmlString string;

type ValidationExceptionType record {
    *XMLExceptionType;
};

type ListFilesActivityInputClass record {
    string fileName;
};

type ReadFileFaultData ReadFileFaultDataType;

type ReadActivityOutputTextClass record {
    fileInfoType fileInfo;
    fileContentTypeTextClass fileContent;
};

type MissingByteCountException MissingByteCountExceptionType;

type ReadActivityInputClass record {
    string fileName;
};

type result string;

type RenameActivityInputClass record {
    string fromFileName;
    string toFileName;
};

type renderAsText boolean;

type fileContentTypeBinary record {
    int binaryContent;
};

type ReadActivityOutputBinaryClass record {
    fileInfoType fileInfo;
    fileContentTypeBinary fileContent;
};

type ActivityErrorDataType XMLRenderException|()|UnsupportedEncodingException|MissingByteCountException;

type XMLParseExceptionType record {
    *XMLExceptionType;
};

type XMLRenderException XMLRenderExceptionType;

type RenameActivityConfig RenameActivityInputClass;

type ReadActivityOutputNoContentClass record {
    fileInfoType fileInfo;
};

type OptionalErrorReport record {
    string StackTrace;
    string Msg;
    string FullClass;
    string Class;
    string ProcessStack;
    string MsgCode;
    anydata Data;
};

type term ();

type FileIOException FileIOExceptionType;

type LogParametersType record {
    string msgCode;
    string loggerName;
    string logLevel;
    string message;
};

type XMLParseException XMLParseExceptionType;

type EventSourceConfigClass record {
    string fileName;
    int pollInterval;
    boolean includeSubDirectories;
    boolean includeCurrent;
    boolean excludeContent;
    string mode;
    string encoding;
    string encodingUsed;
    string sortorder;
    string sortby;
};

type complexTypeFault record {
    string msg;
    string msgCode;
};

// comment
//FIXME: Failed to convert type due to [ParseError] : Expected 1 children, but found: 0
// comment
//<complexType name="RemoveActivityConfigClass" xmlns="http://www.w3.org/2001/XMLSchema"/>
// comment
//<complexType name="RemoveActivityConfigClass" xmlns="http://www.w3.org/2001/XMLSchema"/>
type RemoveActivityConfigClass anydata;

type WaitForFileChangeActivityInput record {
    string key;
    int processTimeout;
};

type ActivityExceptionType record {
    string msg;
    string msgCode;
};

type CreateActivityInputClass record {
    string fileName;
};

type WriteActivityOutputClass record {
    fileInfoType fileInfo;
};

type EventSourceOuputTextClass record {
    string action;
    int timeOccurred;
    fileInfoType fileInfo;
    fileContentTypeTextClass fileContent;
};

type DuplicateKeyException DuplicateKeyExceptionType;

type ListFilesActivityConfig ListFilesActivityInputClass;

type UnsupportedEncodingExceptionType record {
    *XMLExceptionType;
    string encoding;
};

type XMLExceptionType record {
    string msg;
    string msgCode;
};

type CreateActivityOutputClass record {
    fileInfoType fileInfo;
};

type XMLException XMLExceptionType;

type RemoveActivityInputClass record {
    string fileName;
};

type ReadFileFaultDataType FileNotFoundException|UnsupportedEncodingException|FileIOException;

type FileException FileExceptionType;

type ActivityErrorData ActivityErrorDataType;

type LogMessage LogMessageType;

type IllegalCopyException IllegalCopyExceptionType;

type WriteActivityConfigClass record {
    boolean createNewFile;
    boolean append;
    string encoding;
    string compressFile;
};

type ListFilesActivityOutput record {
    files files;
};

type ActivityInput LogParametersType;
