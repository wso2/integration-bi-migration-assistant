import ballerina/data.xmldata;

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type anydatarecord {|@xmldata:Sequence {minOccurs:1,maxOccurs:1}SequenceGroup4 sequenceGroup4;|};
@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.update"}
public type ActivityErrorData record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption choiceOption;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.update"}
public type ActivityErrorDataType record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption choiceOption;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type ActivityExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
public type ActivityInput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityTimedOutException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityTimedOutExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.update"}
public type ChoiceOption record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.update"}
    JDBCConnectionNotFoundException JDBCConnectionNotFoundException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.update"}
    InvalidTimeZoneException InvalidTimeZoneException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.update"}
    JDBCSQLException JDBCSQLException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.update"}
    LoginTimedOutException LoginTimedOutException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.update"}
    InvalidSQLTypeException InvalidSQLTypeException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.update"}
    ActivityTimedOutException ActivityTimedOutException?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type CorrelationValue record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/basic/6.0/Exceptions"}
public type DefaultFault record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
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

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type DuplicatedFieldNameException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type DuplicatedFieldNameExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://www.example.com/namespaces/tns/1535845694732"}
public type Element record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
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

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type InvalidSQLTypeException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type InvalidSQLTypeExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type InvalidTimeZoneException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type InvalidTimeZoneExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCConnectionNotFoundException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCConnectionNotFoundExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCPluginException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCPluginExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCSQLException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCSQLExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
public type LogParametersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type LoginTimedOutException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type LoginTimedOutExceptionType record {|
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

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type PluginExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
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

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+output"}
public type Record record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
public type Request record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
public type RequestType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
public type Response record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
public type SequenceGroup record {|
    @xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 1}
    int FICOScore?;
    @xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 2}
    string Rating?;
    @xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 3}
    int NoOfInquiries?;
|};

@xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
public type SequenceGroup1 record {|
    @xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 1}
    string SSN?;
    @xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 2}
    string FirstName?;
    @xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 3}
    string LastName?;
    @xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 4}
    string DOB?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup2 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    string Content_Length?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 2}
    string Connection?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 3}
    string Pragma?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 4}
    string StatusLine?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 5}
    string Location?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 6}
    string Set_Cookie?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 7}
    string Content_Type?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 8}
    dynamicHeadersType DynamicHeaders?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup3 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    string Name;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 2}
    string Value;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup4 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    dynamicHeadersTypeDetails[] Header?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup5 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    int statusCode;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup6 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    int statusCode;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 2}
    string message?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup7 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    int statusCode;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 2}
    string message?;
|};

@xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
public type SuccessSchema record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type client4XXError record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup6 sequenceGroup6;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type client4XXErrorType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup6 sequenceGroup6;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type column record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type dynamicHeadersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type dynamicHeadersTypeDetails record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpFaultHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpResponseHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpTransportFaultHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpTransportHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpTransportResponseHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcCallActivityInput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Name {value: "jdbcCallActivityInput"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcCallActivityInput1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcCallActivityOutput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Name {value: "jdbcCallActivityOutput"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcCallActivityOutput1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcGeneralActivityInput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Name {value: "jdbcGeneralActivityInput"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcGeneralActivityInput1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcGeneralActivityOutput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Name {value: "jdbcGeneralActivityOutput"}
@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcGeneralActivityOutput1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input"}
public type jdbcQueryActivityInput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+21902290-4882-46a2-8795-b85989c9d7c0+input"}
public type jdbcUpdateActivityInput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcUpdateActivityOutput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type jdbcUpdateOutput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type messageBody record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+output"}
public type resultSet record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type row record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type server5XXError record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type server5XXErrorType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type statusLine record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type statusLineType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type tmessageBody string;

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc"}
public type unknownResultset record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup6 sequenceGroup6;
|};

public type QueryData1 record {|
    int noOfPulls;
    string ssn;
|};

public type QueryData0 record {|
    string ssn;
|};
