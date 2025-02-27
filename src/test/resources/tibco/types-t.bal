import ballerina/http;

type ErrorReport record {
    string StackTrace;
    string Msg;
    string FullClass;
    string Class;
    string ProcessStack;
    string MsgCode;
    anydata Data;
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

type ProcessContext record {
    string JobId;
    string ApplicationName;
    string EngineName;
    string ProcessInstanceId;
    string CustomJobId;
    string TrackingInfo;
};

type anydatarecord {|
...anydata
|};
type OptionalErrorReport OptionalErrorReport;

type ErrorReport ErrorReport;

type FaultDetail FaultDetail;

type ProcessContext ProcessContext;

type CorrelationValue string;

type ActivityExceptionType record {
    string msg;
    string msgCode;
};

type ActivityTimedOutExceptionType record {
    include ActivityExceptionType
};

type DuplicateKeyExceptionType record {
    include ActivityExceptionType
    string duplicateKey;
    string previousJobID;
};

type ActivityException ActivityExceptionType;

type ActivityTimedOutException ActivityTimedOutExceptionType;

type DuplicateKeyException DuplicateKeyExceptionType;

type ActivityErrorDataType ActivityTimedOutException|()|NOT_FOUND|INTERNAL_SERVER_ERROR;

type ActivityErrorData ActivityErrorDataType;
