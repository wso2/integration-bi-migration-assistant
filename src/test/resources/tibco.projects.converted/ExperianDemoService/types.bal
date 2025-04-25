import ballerina/data.xmldata;

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
public type InputElement record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
public type SequenceGroup record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
    @xmldata:SequenceOrder {value: 1}
    string dob;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
    @xmldata:SequenceOrder {value: 2}
    string firstName;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
    @xmldata:SequenceOrder {value: 3}
    string lastName;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
    @xmldata:SequenceOrder {value: 4}
    string ssn;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/json/1535671685533"}
public type ExperianResponseSchemaElementType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/json/1535671685533"}
public type ExperianResponseSchemaElement record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/json/1535671685533"}
public type SequenceGroup1 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/json/1535671685533"}
    @xmldata:SequenceOrder {value: 1}
    int fiCOScore?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/json/1535671685533"}
    @xmldata:SequenceOrder {value: 2}
    string rating?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/json/1535671685533"}
    @xmldata:SequenceOrder {value: 3}
    int noOfInquiries?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type messageBody record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type tmessageBody string;

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpTransportHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpTransportResponseHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpTransportFaultHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type dynamicHeadersTypeDetails record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type dynamicHeadersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup6 sequenceGroup6;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpResponseHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpFaultHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type statusLineType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type statusLine record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type client4XXErrorType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup8 sequenceGroup8;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type client4XXError record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup8 sequenceGroup8;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type server5XXErrorType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup9 sequenceGroup9;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type server5XXError record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup9 sequenceGroup9;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup2 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    string Accept?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 2}
    string Accept_Charset?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 3}
    string Accept_Encoding?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 4}
    string Content_Type?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 5}
    string Content_Length?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 6}
    string Connection?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 7}
    string Cookie?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 8}
    string Pragma?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 9}
    string Authorization?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 10}
    dynamicHeadersType DynamicHeaders?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup3 record {|
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
public type SequenceGroup4 record {|
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
public type SequenceGroup5 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    string Name;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 2}
    string Value;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup6 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    dynamicHeadersTypeDetails[] Header?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup7 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    int statusCode;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup8 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    int statusCode;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 2}
    string message?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup9 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    int statusCode;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 2}
    string message?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ErrorReport record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup10 sequenceGroup10;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type OptionalErrorReport record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup11 sequenceGroup11;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type FaultDetail record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup12 sequenceGroup12;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup13 sequenceGroup13;
|};

@xmldata:Name {value: "anydata"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type Anydata record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup14 sequenceGroup14;
|};

@xmldata:Name {value: "OptionalErrorReport"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type OptionalErrorReport1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup11 sequenceGroup11;
|};

@xmldata:Name {value: "ErrorReport"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ErrorReport1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup10 sequenceGroup10;
|};

@xmldata:Name {value: "FaultDetail"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type FaultDetail1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup12 sequenceGroup12;
|};

@xmldata:Name {value: "ProcessContext"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup13 sequenceGroup13;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type CorrelationValue record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type SequenceGroup10 record {|
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
public type SequenceGroup11 record {|
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
public type SequenceGroup12 record {|
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
public type SequenceGroup13 record {|
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
public type SequenceGroup14 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 1}
    string \#content;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup15 sequenceGroup15;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup15 sequenceGroup15;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityTimedOutExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup15 sequenceGroup15;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityTimedOutException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type DuplicateKeyExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup15 sequenceGroup15;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup16 sequenceGroup16;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type DuplicateKeyException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup16 sequenceGroup16;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type SequenceGroup15 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 1}
    string msg;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 2}
    string msgCode?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type SequenceGroup16 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 1}
    string duplicateKey;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 2}
    string previousJobID?;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type CertificateChainType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup17 sequenceGroup17;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type CertificateTokenType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup18 sequenceGroup18;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type CertificateType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup19 sequenceGroup19;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type SecurityContextType record {|
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    CertificateToken CertificateToken;
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    UsernamePasswordToken UsernamePasswordToken;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type UsernamePasswordTokenType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup20 sequenceGroup20;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type X509CertificateType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup21 sequenceGroup21;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type Certificate record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup19 sequenceGroup19;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type CertificateChain record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup17 sequenceGroup17;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type CertificateToken record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup18 sequenceGroup18;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type SecurityContext record {|
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    CertificateToken CertificateToken;
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    UsernamePasswordToken UsernamePasswordToken;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type UsernamePasswordToken record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup20 sequenceGroup20;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type X509Certificate record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup21 sequenceGroup21;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type SequenceGroup17 record {|
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    @xmldata:SequenceOrder {value: 1}
    Certificate Certificate;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type SequenceGroup18 record {|
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    @xmldata:SequenceOrder {value: 1}
    string CipherSuite?;
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    @xmldata:SequenceOrder {value: 2}
    CertificateChain CertificateChain;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type SequenceGroup19 record {|
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    @xmldata:SequenceOrder {value: 1}
    string SubjectDN?;
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    @xmldata:SequenceOrder {value: 2}
    string IssuerDN?;
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    @xmldata:SequenceOrder {value: 3}
    byte[] Fingerprint?;
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    @xmldata:SequenceOrder {value: 4}
    X509Certificate X509Certificate;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type Password record {|
    @xmldata:Attribute
    string 'type;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type SequenceGroup20 record {|
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    @xmldata:SequenceOrder {value: 1}
    string Username;
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    @xmldata:SequenceOrder {value: 2}
    Password Password?;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
public type SequenceGroup21 record {|
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/bw/security/tokens"}
    @xmldata:SequenceOrder {value: 1}
    byte[] Encoded;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
public type headersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup22 sequenceGroup22;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
public type ContextType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup25 sequenceGroup25;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
public type HTTPEventSourceOutputType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup26 sequenceGroup26;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
public type ProcessStarterOutput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup26 sequenceGroup26;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
public type WaitForHTTPRequestInputType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup27 sequenceGroup27;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
public type input record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup27 sequenceGroup27;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
public type SequenceGroup22 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 1}
    string Accept?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 2}
    string Accept_Charset?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 3}
    string Accept_Encoding?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 4}
    string Content_Type?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 5}
    string Content_Length?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 6}
    string Connection?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 7}
    string Cookie?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 8}
    string Pragma?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
public type SequenceGroup23 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 1}
    string Name;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 2}
    string Value;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
public type SequenceGroup24 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 1}
    dynamicHeadersTypeDetails[] Header?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
public type SequenceGroup25 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 1}
    string RemoteAddress;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
public type SequenceGroup26 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 1}
    string Method?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 2}
    string RequestURI?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 3}
    string HTTPVersion?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 4}
    string PostData?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 5}
    string QueryString?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 6}
    string Header?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 7}
    string Protocol?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 8}
    string Port?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 9}
    headersType Headers?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 10}
    dynamicHeadersType DynamicHeaders?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 11}
    mimeEnvelopeElement mimeEnvelopeElement;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 12}
    ContextType Context;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
public type SequenceGroup27 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 1}
    string 'key?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 2}
    int processTimeout?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
public type ActivityInputType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup31 sequenceGroup31;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
public type ResponseActivityInput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup31 sequenceGroup31;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
public type SequenceGroup28 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    @xmldata:SequenceOrder {value: 1}
    string StatusLine?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    @xmldata:SequenceOrder {value: 2}
    string Content_Type?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    @xmldata:SequenceOrder {value: 3}
    string Set_Cookie?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    @xmldata:SequenceOrder {value: 4}
    string Pragma?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    @xmldata:SequenceOrder {value: 5}
    string Location?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
public type SequenceGroup29 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    @xmldata:SequenceOrder {value: 1}
    string Name;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    @xmldata:SequenceOrder {value: 2}
    string Value;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
public type SequenceGroup30 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    @xmldata:SequenceOrder {value: 1}
    dynamicHeadersTypeDetails[] Header?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
public type SequenceGroup31 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    @xmldata:SequenceOrder {value: 1}
    byte[] binaryContent?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    @xmldata:SequenceOrder {value: 2}
    string asciiContent?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    @xmldata:SequenceOrder {value: 3}
    headersType Headers?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    @xmldata:SequenceOrder {value: 4}
    dynamicHeadersType DynamicHeaders?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    @xmldata:SequenceOrder {value: 5}
    mimeEnvelopeElement mimeEnvelopeElement;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type StatusLineType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup32 sequenceGroup32;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HeadersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup33 sequenceGroup33;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpMessageType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup34 sequenceGroup34;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type BaseExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup35 sequenceGroup35;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup35 sequenceGroup35;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpClientExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup36 sequenceGroup36;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpCommunicationExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup35 sequenceGroup35;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpResponseExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup35 sequenceGroup35;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup36 sequenceGroup36;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpServerExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup35 sequenceGroup35;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup36 sequenceGroup36;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpClientException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpCommunicationException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpResponseException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup36 sequenceGroup36;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpServerException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type SequenceGroup32 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string httpVersion;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 2}
    int statusCode;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 3}
    string reasonPhrase;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type SequenceGroup33 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string \#content;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type SequenceGroup34 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 1}
    HeadersType headers;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 2}
    byte[] binaryContent?;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 3}
    string asciiContent?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type SequenceGroup35 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string msg;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 2}
    string msgCode;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type SequenceGroup36 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 1}
    StatusLineType statusLine;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 2}
    HttpMessageType httpMessage?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input"}
public type jdbcQueryActivityInput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup37 sequenceGroup37;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input"}
public type SequenceGroup37 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input"}
    @xmldata:SequenceOrder {value: 1}
    string ssn;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input"}
    @xmldata:SequenceOrder {value: 2}
    string ServerTimeZone?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input"}
    @xmldata:SequenceOrder {value: 3}
    int timeout?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input"}
    @xmldata:SequenceOrder {value: 4}
    int maxRows?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+output"}
public type resultSet record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup39 sequenceGroup39;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+output"}
public type SequenceGroup38 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+output"}
    @xmldata:SequenceOrder {value: 1}
    string firstname?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+output"}
    @xmldata:SequenceOrder {value: 2}
    string lastname?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+output"}
    @xmldata:SequenceOrder {value: 3}
    string ssn?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+output"}
    @xmldata:SequenceOrder {value: 4}
    string dateofBirth?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+output"}
    @xmldata:SequenceOrder {value: 5}
    int ficoscore?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+output"}
    @xmldata:SequenceOrder {value: 6}
    string rating?;
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+output"}
    @xmldata:SequenceOrder {value: 7}
    int numofpulls?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+output"}
public type Record record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup38 sequenceGroup38;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+output"}
public type SequenceGroup39 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+output"}
    @xmldata:SequenceOrder {value: 1}
    Record[] Record?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type PluginExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup40 sequenceGroup40;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCPluginException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCPluginExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup40 sequenceGroup40;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type InvalidSQLTypeException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup41 sequenceGroup41;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type InvalidSQLTypeExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup40 sequenceGroup40;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup41 sequenceGroup41;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCConnectionNotFoundException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup42 sequenceGroup42;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCConnectionNotFoundExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup40 sequenceGroup40;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup42 sequenceGroup42;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type DuplicatedFieldNameException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup43 sequenceGroup43;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type DuplicatedFieldNameExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup40 sequenceGroup40;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup43 sequenceGroup43;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type InvalidTimeZoneException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup44 sequenceGroup44;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type InvalidTimeZoneExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup40 sequenceGroup40;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup44 sequenceGroup44;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCSQLException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup45 sequenceGroup45;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type JDBCSQLExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup40 sequenceGroup40;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup45 sequenceGroup45;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type LoginTimedOutException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type LoginTimedOutExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup40 sequenceGroup40;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type SequenceGroup40 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string msg;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    @xmldata:SequenceOrder {value: 2}
    string msgCode?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type SequenceGroup41 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string typeName;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type SequenceGroup42 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string jdbcConnection;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type SequenceGroup43 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string fieldName;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type SequenceGroup44 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string timeZone;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
public type SequenceGroup45 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string sqlState;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    @xmldata:SequenceOrder {value: 2}
    string detailStr;
|};

@xmldata:Namespace {uri: "activity.jsonParser.input+f396d921-0bc7-459d-ae81-0eb1a0f94723+ActivityInputType"}
public type ActivityInputClassType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup46 sequenceGroup46;
|};

@xmldata:Namespace {uri: "activity.jsonParser.input+f396d921-0bc7-459d-ae81-0eb1a0f94723+ActivityInputType"}
public type ActivityInputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup46 sequenceGroup46;
|};

@xmldata:Namespace {uri: "activity.jsonParser.input+f396d921-0bc7-459d-ae81-0eb1a0f94723+ActivityInputType"}
public type SequenceGroup46 record {|
    @xmldata:Namespace {uri: "activity.jsonParser.input+f396d921-0bc7-459d-ae81-0eb1a0f94723+ActivityInputType"}
    @xmldata:SequenceOrder {value: 1}
    string jsonString;
|};

@xmldata:Name {value: "JSONActivityException"}
@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONActivityException1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup47 sequenceGroup47;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONActivityException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup47 sequenceGroup47;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type UnsupportedEncodingException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup48 sequenceGroup48;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type UnsupportedEncodingExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup47 sequenceGroup47;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup48 sequenceGroup48;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONParserExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup47 sequenceGroup47;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONParserException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONTransformExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup47 sequenceGroup47;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONTransformException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONRenderExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup47 sequenceGroup47;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONRenderException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONRestExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup47 sequenceGroup47;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONRestException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type ValidationExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup47 sequenceGroup47;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type ValidationException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type SequenceGroup47 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string msg;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    @xmldata:SequenceOrder {value: 2}
    string msgCode?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type SequenceGroup48 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string encoding;
|};

@xmldata:Namespace {uri: "activity.jsonRender.output+8ccea717-63a9-4d35-945d-ec9437e37100+ActivityOutputType"}
public type ActivityOutputClassType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup49 sequenceGroup49;
|};

@xmldata:Namespace {uri: "activity.jsonRender.output+8ccea717-63a9-4d35-945d-ec9437e37100+ActivityOutputType"}
public type ActivityOutputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup49 sequenceGroup49;
|};

@xmldata:Namespace {uri: "activity.jsonRender.output+8ccea717-63a9-4d35-945d-ec9437e37100+ActivityOutputType"}
public type SequenceGroup49 record {|
    @xmldata:Namespace {uri: "activity.jsonRender.output+8ccea717-63a9-4d35-945d-ec9437e37100+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 1}
    string jsonString;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.http.sendHTTPResponse"}
public type ActivityErrorData record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption choiceOption;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.http.sendHTTPResponse"}
public type ActivityErrorDataType record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption choiceOption;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.http.sendHTTPResponse"}
public type ChoiceOption record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.http.sendHTTPResponse"}
    HttpCommunicationException HttpCommunicationException?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.JDBCQuery"}
public type ChoiceOption1 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.JDBCQuery"}
    JDBCConnectionNotFoundException JDBCConnectionNotFoundException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.JDBCQuery"}
    InvalidTimeZoneException InvalidTimeZoneException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.JDBCQuery"}
    JDBCSQLException JDBCSQLException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.JDBCQuery"}
    LoginTimedOutException LoginTimedOutException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.JDBCQuery"}
    InvalidSQLTypeException InvalidSQLTypeException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.JDBCQuery"}
    DuplicatedFieldNameException DuplicatedFieldNameException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.JDBCQuery"}
    ActivityTimedOutException ActivityTimedOutException?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.restjson.JsonParser"}
public type ChoiceOption2 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.restjson.JsonParser"}
    JSONParserException JSONParserException?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.restjson.JsonRender"}
public type ChoiceOption3 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.restjson.JsonRender"}
    JSONRenderException JSONRenderException?;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
public type mimePartType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup50 sequenceGroup50;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
public type mimeHeadersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup51 sequenceGroup51;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
public type mimeEnvelopeElementType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup52 sequenceGroup52;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
public type mimeEnvelopeElement record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup52 sequenceGroup52;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
public type SequenceGroup50 record {|
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
    @xmldata:SequenceOrder {value: 1}
    mimeHeadersType mimeHeaders;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
public type SequenceGroup51 record {|
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
    @xmldata:SequenceOrder {value: 1}
    string content_disposition?;
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
    @xmldata:SequenceOrder {value: 2}
    string content_type;
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
    @xmldata:SequenceOrder {value: 3}
    string content_transfer_encoding?;
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
    @xmldata:SequenceOrder {value: 4}
    string content_id?;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
public type SequenceGroup52 record {|
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
    @xmldata:SequenceOrder {value: 1}
    mimePartType[] mimePart?;
|};
