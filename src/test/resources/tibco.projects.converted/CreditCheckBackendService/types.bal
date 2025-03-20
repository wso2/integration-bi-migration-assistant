import ballerina/data.xmldata;

type DuplicatedFieldNameException DuplicatedFieldNameExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type httpTransportResponseHeaders record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Content_Length;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Connection;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Pragma;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string StatusLine;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Location;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Set_Cookie;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Content_Type;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    dynamicHeadersType DynamicHeaders;
};

type anyType anydata;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
type ActivityTimedOutExceptionType record {
    *ActivityExceptionType;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type server5XXErrorType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    int statusCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string message;
};

type InvalidTimeZoneException InvalidTimeZoneExceptionType;

type httpHeaders httpTransportHeaders;

type CorrelationValue string;

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

type resultSet ();

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

type jdbcQueryActivityInput ();

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type statusLineType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    int statusCode;
};

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type InvalidSQLTypeExceptionType record {
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string typeName;
};

type jdbcUpdateActivityOutput jdbcUpdateOutput;

type DefaultFault ();

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type dynamicHeadersType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    dynamicHeadersTypeDetails[] Header;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type httpTransportFaultHeaders record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Content_Length;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Connection;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Pragma;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string StatusLine;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Location;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Set_Cookie;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Content_Type;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    dynamicHeadersType DynamicHeaders;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type dynamicHeadersTypeDetails record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Name;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Value;
};

type messageBody tmessageBody;

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type JDBCSQLExceptionType record {
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string sqlState;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string detailStr;
};

type ActivityException ActivityExceptionType;

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type JDBCPluginExceptionType record {
    *PluginExceptionType;
};

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type JDBCConnectionNotFoundExceptionType record {
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string jdbcConnection;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
type unknownResultset record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    row[] row;
};

type InvalidSQLTypeException InvalidSQLTypeExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
type jdbcCallActivityInput record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    anyType inputSet;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    string ServerTimeZone;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    int timeout;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    int maxRows;
};

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type LoginTimedOutExceptionType record {
    *JDBCPluginExceptionType;
};

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

type jdbcUpdateActivityInput ();

type httpResponseHeaders httpTransportResponseHeaders;

type QueryData1 record {
    int noOfPulls;
    string ssn;
};

type tmessageBody string;

@xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
type SuccessSchema record {
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    int FICOScore;
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    string Rating;
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    int NoOfInquiries;
};

type QueryData0 record {
    string ssn;
};

type client4XXError client4XXErrorType;

@xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
type RequestType record {
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    string SSN;
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    string FirstName;
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    string LastName;
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    string DOB;
};

type ActivityTimedOutException ActivityTimedOutExceptionType;

type JDBCConnectionNotFoundException JDBCConnectionNotFoundExceptionType;

type statusLine statusLineType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
type DuplicateKeyExceptionType record {
    *ActivityExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string duplicateKey;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string previousJobID;
};

type Element ();

type JDBCSQLException JDBCSQLExceptionType;

type ActivityErrorDataType JDBCConnectionNotFoundException|()|InvalidTimeZoneException|JDBCSQLException|LoginTimedOutException|InvalidSQLTypeException|ActivityTimedOutException;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type httpTransportHeaders record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Accept;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Accept_Charset;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Accept_Encoding;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Content_Type;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Content_Length;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Connection;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Cookie;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Pragma;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Authorization;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    dynamicHeadersType DynamicHeaders;
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

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
type row record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    column[] column;
};

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

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type InvalidTimeZoneExceptionType record {
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string timeZone;
};

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type ActivityExceptionType record {
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string msgCode;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
type jdbcGeneralActivityOutput record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    int[] noOfUpdates;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    unknownResultset[] unknownResultset;
};

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type PluginExceptionType record {
    *ActivityExceptionType;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
type column record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    string name;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    anyType value;
};

type Request RequestType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
type jdbcCallActivityOutput record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    string outputSet;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    string UnResolvedResultSets;
};

type server5XXError server5XXErrorType;

type DuplicateKeyException DuplicateKeyExceptionType;

type LoginTimedOutException LoginTimedOutExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
type jdbcUpdateOutput record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    int noOfUpdates;
};

type Response SuccessSchema;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type client4XXErrorType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    int statusCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string message;
};

type JDBCPluginException JDBCPluginExceptionType;

type ActivityErrorData ActivityErrorDataType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
type jdbcGeneralActivityInput record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    string statement;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    string ServerTimeZone;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    int timeout;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    int maxRows;
};

type ActivityInput LogParametersType;

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type DuplicatedFieldNameExceptionType record {
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string fieldName;
};

type httpFaultHeaders httpTransportFaultHeaders;
