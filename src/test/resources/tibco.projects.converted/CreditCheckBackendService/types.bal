import ballerina/data.xmldata;

public type DuplicatedFieldNameException DuplicatedFieldNameExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
public type httpTransportResponseHeaders record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Content_Length?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Connection?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Pragma?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string StatusLine?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Location?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Set_Cookie?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Content_Type?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    dynamicHeadersType DynamicHeaders?;
|};

public type anyType anydata;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityTimedOutExceptionType record {|
    *ActivityExceptionType;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
public type server5XXErrorType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    int statusCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string message?;
|};

public type httpHeaders httpTransportHeaders;

public type InvalidTimeZoneException InvalidTimeZoneExceptionType;

public type CorrelationValue string;

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

//FIXME: Failed to convert type due to [ParseError] : Only simple content is supported for anonymous types
//<element maxOccurs="unbounded" minOccurs="0" name="Record" xmlns="http://www.w3.org/2001/XMLSchema">
//    <complexType>
//        <sequence>
//            <element maxOccurs="1" minOccurs="0" name="firstname" nillable="false" type="string"/>
//            <element maxOccurs="1" minOccurs="0" name="lastname" nillable="false" type="string"/>
//            <element maxOccurs="1" minOccurs="0" name="ssn" nillable="false" type="string"/>
//            <element maxOccurs="1" minOccurs="0" name="dateofBirth" nillable="false" type="string"/>
//            <element maxOccurs="1" minOccurs="0" name="ficoscore" nillable="false" type="int"/>
//            <element maxOccurs="1" minOccurs="0" name="rating" nillable="false" type="string"/>
//            <element maxOccurs="1" minOccurs="0" name="numofpulls" nillable="false" type="int"/>
//        </sequence>
//    </complexType>
//</element>

//<element name="resultSet" xmlns="http://www.w3.org/2001/XMLSchema">
//    <complexType>
//        <sequence>
//            <element maxOccurs="unbounded" minOccurs="0" name="Record">
//                <complexType>
//                    <sequence>
//                        <element maxOccurs="1" minOccurs="0" name="firstname" nillable="false" type="string"/>
//                        <element maxOccurs="1" minOccurs="0" name="lastname" nillable="false" type="string"/>
//                        <element maxOccurs="1" minOccurs="0" name="ssn" nillable="false" type="string"/>
//                        <element maxOccurs="1" minOccurs="0" name="dateofBirth" nillable="false" type="string"/>
//                        <element maxOccurs="1" minOccurs="0" name="ficoscore" nillable="false" type="int"/>
//                        <element maxOccurs="1" minOccurs="0" name="rating" nillable="false" type="string"/>
//                        <element maxOccurs="1" minOccurs="0" name="numofpulls" nillable="false" type="int"/>
//                    </sequence>
//                </complexType>
//            </element>
//        </sequence>
//    </complexType>
//</element>
public type resultSet anydata;

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

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input"}
public type jdbcQueryActivityInput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input"}
    string ssn;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input"}
    string ServerTimeZone?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input"}
    int timeout?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input"}
    int maxRows?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
public type statusLineType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    int statusCode;
|};

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type InvalidSQLTypeExceptionType record {|
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string typeName;
|};

public type jdbcUpdateActivityOutput jdbcUpdateOutput;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/basic/6.0/Exceptions"}
public type DefaultFault record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/basic/6.0/Exceptions"}
    string message?;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/basic/6.0/Exceptions"}
    string msgCode?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
public type dynamicHeadersType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    dynamicHeadersTypeDetails[] Header;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
public type httpTransportFaultHeaders record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Content_Length?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Connection?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Pragma?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string StatusLine?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Location?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Set_Cookie?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Content_Type?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    dynamicHeadersType DynamicHeaders?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
public type dynamicHeadersTypeDetails record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Name;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Value;
|};

public type messageBody tmessageBody;

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCSQLExceptionType record {|
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string sqlState;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string detailStr;
|};

public type ActivityException ActivityExceptionType;

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCPluginExceptionType record {|
    *PluginExceptionType;
|};

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCConnectionNotFoundExceptionType record {|
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string jdbcConnection;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type unknownResultset record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    row[] row;
|};

public type InvalidSQLTypeException InvalidSQLTypeExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcCallActivityInput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    anyType inputSet?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    string ServerTimeZone?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    int timeout?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    int maxRows?;
|};

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type LoginTimedOutExceptionType record {|
    *JDBCPluginExceptionType;
|};

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

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+21902290-4882-46a2-8795-b85989c9d7c0+input"}
public type jdbcUpdateActivityInput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+21902290-4882-46a2-8795-b85989c9d7c0+input"}
    int noOfPulls;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+21902290-4882-46a2-8795-b85989c9d7c0+input"}
    string ssn;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+21902290-4882-46a2-8795-b85989c9d7c0+input"}
    string ServerTimeZone?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+21902290-4882-46a2-8795-b85989c9d7c0+input"}
    int timeout?;
|};

public type httpResponseHeaders httpTransportResponseHeaders;

public type QueryData1 record {|
    int noOfPulls;
    string ssn;
|};

public type tmessageBody string;

@xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
public type SuccessSchema record {|
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    int FICOScore?;
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    string Rating?;
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    int NoOfInquiries?;
|};

public type QueryData0 record {|
    string ssn;
|};

public type client4XXError client4XXErrorType;

@xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
public type RequestType record {|
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    string SSN?;
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    string FirstName?;
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    string LastName?;
    @xmldata:Namespace {prefix: "tns", uri: "/T1535753828744Converted/JsonSchema"}
    string DOB?;
|};

public type ActivityTimedOutException ActivityTimedOutExceptionType;

public type JDBCConnectionNotFoundException JDBCConnectionNotFoundExceptionType;

public type statusLine statusLineType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type DuplicateKeyExceptionType record {|
    *ActivityExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string duplicateKey;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string previousJobID?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.example.com/namespaces/tns/1535845694732"}
public type Element record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.example.com/namespaces/tns/1535845694732"}
    string ssn;
|};

public type JDBCSQLException JDBCSQLExceptionType;

public type ActivityErrorDataType JDBCConnectionNotFoundException|()|InvalidTimeZoneException|JDBCSQLException|LoginTimedOutException|InvalidSQLTypeException|ActivityTimedOutException;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
public type httpTransportHeaders record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Accept?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Accept_Charset?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Accept_Encoding?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Content_Type?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Content_Length?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Connection?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Cookie?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Pragma?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string Authorization?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    dynamicHeadersType DynamicHeaders?;
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

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type row record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    column[] column;
|};

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

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type InvalidTimeZoneExceptionType record {|
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string timeZone;
|};

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type ActivityExceptionType record {|
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string msgCode?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcGeneralActivityOutput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    int[] noOfUpdates;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    unknownResultset[] unknownResultset;
|};

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type PluginExceptionType record {|
    *ActivityExceptionType;
|};

public type Request RequestType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type column record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    string name;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    anyType value;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcCallActivityOutput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    string outputSet?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    string UnResolvedResultSets?;
|};

public type server5XXError server5XXErrorType;

public type DuplicateKeyException DuplicateKeyExceptionType;

public type LoginTimedOutException LoginTimedOutExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcUpdateOutput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    int noOfUpdates;
|};

public type Response SuccessSchema;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
public type client4XXErrorType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    int statusCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string message?;
|};

public type JDBCPluginException JDBCPluginExceptionType;

public type ActivityErrorData ActivityErrorDataType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcGeneralActivityInput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    string statement;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    string ServerTimeZone?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    int timeout?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
    int maxRows?;
|};

public type ActivityInput LogParametersType;

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type DuplicatedFieldNameExceptionType record {|
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string fieldName;
|};

public type httpFaultHeaders httpTransportFaultHeaders;
