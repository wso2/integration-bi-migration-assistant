type DuplicatedFieldNameException DuplicatedFieldNameExceptionType;

type httpTransportResponseHeaders record {
    string Content_Length;
    string Connection;
    string Pragma;
    string StatusLine;
    string Location;
    string Set_Cookie;
    string Content_Type;
    dynamicHeadersType DynamicHeaders;
};

type anyType anydata;

type ActivityTimedOutExceptionType record {
    *ActivityExceptionType;
};

type server5XXErrorType record {
    int statusCode;
    string message;
};

type InvalidTimeZoneException InvalidTimeZoneExceptionType;

type httpHeaders httpTransportHeaders;

type CorrelationValue string;

type ProcessContext record {
    string JobId;
    string ApplicationName;
    string EngineName;
    string ProcessInstanceId;
    string CustomJobId;
    string TrackingInfo;
};

type resultSet ();

type ErrorReport record {
    string StackTrace;
    string Msg;
    string FullClass;
    string Class;
    string ProcessStack;
    string MsgCode;
    anydata Data;
};

type jdbcQueryActivityInput ();

type statusLineType record {
    int statusCode;
};

type InvalidSQLTypeExceptionType record {
    *JDBCPluginExceptionType;
    string typeName;
};

type jdbcUpdateActivityOutput jdbcUpdateOutput;

type DefaultFault ();

type dynamicHeadersType record {
    dynamicHeadersTypeDetails Header;
};

type httpTransportFaultHeaders record {
    string Content_Length;
    string Connection;
    string Pragma;
    string StatusLine;
    string Location;
    string Set_Cookie;
    string Content_Type;
    dynamicHeadersType DynamicHeaders;
};

type dynamicHeadersTypeDetails record {
    string Name;
    string Value;
};

type messageBody tmessageBody;

type JDBCSQLExceptionType record {
    *JDBCPluginExceptionType;
    string sqlState;
    string detailStr;
};

type ActivityException ActivityExceptionType;

type JDBCPluginExceptionType record {
    *PluginExceptionType;
};

type JDBCConnectionNotFoundExceptionType record {
    *JDBCPluginExceptionType;
    string jdbcConnection;
};

type unknownResultset record {
    row row;
};

type InvalidSQLTypeException InvalidSQLTypeExceptionType;

type jdbcCallActivityInput record {
    anyType inputSet;
    string ServerTimeZone;
    int timeout;
    int maxRows;
};

type LoginTimedOutExceptionType record {
    *JDBCPluginExceptionType;
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

type jdbcUpdateActivityInput ();

type httpResponseHeaders httpTransportResponseHeaders;

type tmessageBody string;

type SuccessSchema record {
    int FICOScore;
    string Rating;
    int NoOfInquiries;
};

type client4XXError client4XXErrorType;

type RequestType record {
    string SSN;
    string FirstName;
    string LastName;
    string DOB;
};

type ActivityTimedOutException ActivityTimedOutExceptionType;

type JDBCConnectionNotFoundException JDBCConnectionNotFoundExceptionType;

type statusLine statusLineType;

type DuplicateKeyExceptionType record {
    *ActivityExceptionType;
    string duplicateKey;
    string previousJobID;
};

type Element ();

type JDBCSQLException JDBCSQLExceptionType;

type ActivityErrorDataType JDBCConnectionNotFoundException|()|InvalidTimeZoneException|JDBCSQLException|LoginTimedOutException|InvalidSQLTypeException|ActivityTimedOutException;

type httpTransportHeaders record {
    string Accept;
    string Accept_Charset;
    string Accept_Encoding;
    string Content_Type;
    string Content_Length;
    string Connection;
    string Cookie;
    string Pragma;
    string Authorization;
    dynamicHeadersType DynamicHeaders;
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

type row record {
    column column;
};

type LogParametersType record {
    string msgCode;
    string loggerName;
    string logLevel;
    string message;
};

type InvalidTimeZoneExceptionType record {
    *JDBCPluginExceptionType;
    string timeZone;
};

type ActivityExceptionType record {
    string msg;
    string msgCode;
};

type jdbcGeneralActivityOutput record {
    int noOfUpdates;
    unknownResultset unknownResultset;
};

type PluginExceptionType record {
    *ActivityExceptionType;
};

type column record {
    string name;
    anyType value;
};

type Request RequestType;

type jdbcCallActivityOutput record {
    string outputSet;
    string UnResolvedResultSets;
};

type server5XXError server5XXErrorType;

type DuplicateKeyException DuplicateKeyExceptionType;

type LoginTimedOutException LoginTimedOutExceptionType;

type jdbcUpdateOutput record {
    int noOfUpdates;
};

type Response SuccessSchema;

type client4XXErrorType record {
    int statusCode;
    string message;
};

type JDBCPluginException JDBCPluginExceptionType;

type ActivityErrorData ActivityErrorDataType;

type jdbcGeneralActivityInput record {
    string statement;
    string ServerTimeZone;
    int timeout;
    int maxRows;
};

type ActivityInput LogParametersType;

type DuplicatedFieldNameExceptionType record {
    *JDBCPluginExceptionType;
    string fieldName;
};

type httpFaultHeaders httpTransportFaultHeaders;
