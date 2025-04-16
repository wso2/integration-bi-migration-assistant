import ballerina/data.xmldata;

@xmldata:Name {value: "SuccessSchema"}
@xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
public type SuccessSchema1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
public type SuccessSchema record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Name {value: "GiveNewSchemaNameHere"}
@xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
public type GiveNewSchemaNameHere1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
public type GiveNewSchemaNameHere record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Name {value: "CreditScoreSuccessSchema"}
@xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
public type CreditScoreSuccessSchema1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
public type CreditScoreSuccessSchema record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
public type SequenceGroup record {|
    @xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 1}
    int FICOScore?;
    @xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 2}
    int NoOfInquiries?;
    @xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 3}
    string Rating?;
|};

@xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
public type SequenceGroup1 record {|
    @xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 1}
    string DOB?;
    @xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 2}
    string FirstName?;
    @xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 3}
    string LastName?;
    @xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 4}
    string SSN?;
|};

@xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
public type SequenceGroup2 record {|
    @xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 1}
    SuccessSchema EquifaxResponse?;
    @xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 2}
    SuccessSchema ExperianResponse?;
    @xmldata:Namespace {uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    @xmldata:SequenceOrder {value: 3}
    SuccessSchema TransUnionResponse?;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/GetCreditDetail/headerParameters"}
public type getcreditdetailGetHeaderType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/GetCreditDetail/headerParameters"}
public type getcreditdetailGetHeader record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/GetCreditDetail/headerParameters"}
public type SequenceGroup3 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/GetCreditDetail/headerParameters"}
    @xmldata:SequenceOrder {value: 1}
    string \#content;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/y54cuadtcxtfstqs3rux2gfdaxppoqgc/parameters"}
public type creditscorePostParameters record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/y54cuadtcxtfstqs3rux2gfdaxppoqgc/parameters"}
public type SequenceGroup4 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/y54cuadtcxtfstqs3rux2gfdaxppoqgc/parameters"}
    @xmldata:SequenceOrder {value: 1}
    boolean skipvalidation?;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/Creditscore/parameters"}
public type creditscoreGetParameters record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup6 sequenceGroup6;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/Creditscore/parameters"}
public type SequenceGroup5 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/Creditscore/parameters"}
    @xmldata:SequenceOrder {value: 1}
    string \#content;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/Creditscore/parameters"}
public type SequenceGroup6 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/Creditscore/parameters"}
    @xmldata:SequenceOrder {value: 1}
    string DOB?;
    @xmldata:Namespace {uri: "http://xmlns.example.com/Creditscore/parameters"}
    @xmldata:SequenceOrder {value: 2}
    string FirstName?;
    @xmldata:Namespace {uri: "http://xmlns.example.com/Creditscore/parameters"}
    @xmldata:SequenceOrder {value: 3}
    string LastName?;
    @xmldata:Namespace {uri: "http://xmlns.example.com/Creditscore/parameters"}
    @xmldata:SequenceOrder {value: 4}
    string SSN?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
public type InputElement record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
public type SequenceGroup7 record {|
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
    SequenceGroup8 sequenceGroup8;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/json/1535671685533"}
public type ExperianResponseSchemaElement record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup8 sequenceGroup8;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/json/1535671685533"}
public type SequenceGroup8 record {|
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

@xmldata:Namespace {uri: "http://xmlns.example.com/GetCreditDetail/parameters"}
public type getcreditdetailGetParameters record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup9 sequenceGroup9;
|};

@xmldata:Namespace {uri: "http://xmlns.example.com/GetCreditDetail/parameters"}
public type SequenceGroup9 record {|
    @xmldata:Namespace {uri: "http://xmlns.example.com/GetCreditDetail/parameters"}
    @xmldata:SequenceOrder {value: 1}
    string \#content;
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
    SequenceGroup10 sequenceGroup10;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpTransportResponseHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup11 sequenceGroup11;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpTransportFaultHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup12 sequenceGroup12;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type dynamicHeadersTypeDetails record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup13 sequenceGroup13;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type dynamicHeadersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup14 sequenceGroup14;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup10 sequenceGroup10;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpResponseHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup11 sequenceGroup11;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpFaultHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup12 sequenceGroup12;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type statusLineType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup15 sequenceGroup15;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type statusLine record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup15 sequenceGroup15;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type client4XXErrorType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup16 sequenceGroup16;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type client4XXError record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup16 sequenceGroup16;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type server5XXErrorType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup17 sequenceGroup17;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type server5XXError record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup17 sequenceGroup17;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup10 record {|
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
public type SequenceGroup11 record {|
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
public type SequenceGroup12 record {|
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
public type SequenceGroup13 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    string Name;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 2}
    string Value;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup14 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    dynamicHeadersTypeDetails[] Header?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup15 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    int statusCode;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup16 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 1}
    int statusCode;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
    @xmldata:SequenceOrder {value: 2}
    string message?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup17 record {|
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
    SequenceGroup18 sequenceGroup18;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type OptionalErrorReport record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup19 sequenceGroup19;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type FaultDetail record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup20 sequenceGroup20;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup21 sequenceGroup21;
|};

@xmldata:Name {value: "anydata"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type Anydata record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup22 sequenceGroup22;
|};

@xmldata:Name {value: "OptionalErrorReport"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type OptionalErrorReport1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup19 sequenceGroup19;
|};

@xmldata:Name {value: "ErrorReport"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ErrorReport1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup18 sequenceGroup18;
|};

@xmldata:Name {value: "FaultDetail"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type FaultDetail1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup20 sequenceGroup20;
|};

@xmldata:Name {value: "ProcessContext"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup21 sequenceGroup21;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type CorrelationValue record {|
    string \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type SequenceGroup18 record {|
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
public type SequenceGroup19 record {|
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
public type SequenceGroup20 record {|
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
public type SequenceGroup21 record {|
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
public type SequenceGroup22 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 1}
    string \#content;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup23 sequenceGroup23;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup23 sequenceGroup23;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityTimedOutExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup23 sequenceGroup23;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityTimedOutException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type DuplicateKeyExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup23 sequenceGroup23;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup24 sequenceGroup24;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type DuplicateKeyException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup24 sequenceGroup24;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type SequenceGroup23 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 1}
    string msg;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 2}
    string msgCode?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type SequenceGroup24 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 1}
    string duplicateKey;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 2}
    string previousJobID?;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
public type mimePartType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup25 sequenceGroup25;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
public type mimeHeadersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup26 sequenceGroup26;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
public type mimeEnvelopeElementType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup27 sequenceGroup27;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
public type mimeEnvelopeElement record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup27 sequenceGroup27;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
public type SequenceGroup25 record {|
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
    @xmldata:SequenceOrder {value: 1}
    mimeHeadersType mimeHeaders;
|};

@xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
public type SequenceGroup26 record {|
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
public type SequenceGroup27 record {|
    @xmldata:Namespace {uri: "http://xmlns.tibco.com/encodings/mime"}
    @xmldata:SequenceOrder {value: 1}
    mimePartType[] mimePart?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type inputHeadersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup28 sequenceGroup28;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type DynamicHeaders record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup29 sequenceGroup29;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type Header record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup30 sequenceGroup30;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type RequestActivityInput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup32 sequenceGroup32;
|};

@xmldata:Name {value: "RequestActivityInput"}
@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type RequestActivityInput1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup32 sequenceGroup32;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type parametersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup33 sequenceGroup33;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type SequenceGroup28 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 1}
    string Accept?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 2}
    string Accept_Charset?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 3}
    string Accept_Encoding?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 4}
    string Content_Type?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 5}
    string Cookie?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 6}
    string Pragma?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type SequenceGroup29 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 1}
    Header[] Header?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type SequenceGroup30 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 1}
    string Name;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 2}
    string Value;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type SequenceGroup31 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 1}
    string httpVersion?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 2}
    int statusCode?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 3}
    string reasonPhrase?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type SequenceGroup32 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 1}
    string Host?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 2}
    int Port?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 3}
    string Method?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 4}
    string RequestURI?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 5}
    string PostData?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 6}
    string QueryString?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 7}
    int Timeout?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 8}
    inputHeadersType Headers?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 9}
    DynamicHeaders DynamicHeaders?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 10}
    parametersType parameters?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 11}
    mimeEnvelopeElement mimeEnvelopeElement;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type SequenceGroup33 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    @xmldata:SequenceOrder {value: 1}
    string ssn?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
public type outputHeadersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup34 sequenceGroup34;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
public type RequestActivityOutput record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup38 sequenceGroup38;
|};

@xmldata:Name {value: "RequestActivityOutput"}
@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
public type RequestActivityOutput1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup38 sequenceGroup38;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
public type SequenceGroup34 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 1}
    string Allow?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 2}
    string Content_Type?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 3}
    string Content_Length?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 4}
    string Content_Encoding?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 5}
    string Date?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 6}
    string Location?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 7}
    string[] Set_Cookie?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 8}
    string Pragma?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
public type SequenceGroup35 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 1}
    Header[] Header?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
public type SequenceGroup36 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 1}
    string Name;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 2}
    string Value;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
public type SequenceGroup37 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 1}
    string httpVersion?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 2}
    int statusCode?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 3}
    string reasonPhrase?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
public type SequenceGroup38 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 1}
    string Header?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 2}
    statusLineType statusLine?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 3}
    byte[] binaryContent?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 4}
    string asciiContent?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 5}
    string filePath?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 6}
    outputHeadersType Headers?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 7}
    DynamicHeaders DynamicHeaders?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    @xmldata:SequenceOrder {value: 8}
    mimeEnvelopeElement mimeEnvelopeElement;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type StatusLineType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup39 sequenceGroup39;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HeadersType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup40 sequenceGroup40;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpMessageType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup41 sequenceGroup41;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type BaseExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup42 sequenceGroup42;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup42 sequenceGroup42;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpClientExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup43 sequenceGroup43;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpCommunicationExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup42 sequenceGroup42;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpResponseExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup42 sequenceGroup42;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup43 sequenceGroup43;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpServerExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup42 sequenceGroup42;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup43 sequenceGroup43;
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
    SequenceGroup43 sequenceGroup43;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpServerException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type SequenceGroup39 record {|
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
public type SequenceGroup40 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string \#content;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type SequenceGroup41 record {|
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
public type SequenceGroup42 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string msg;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 2}
    string msgCode;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type SequenceGroup43 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 1}
    StatusLineType statusLine;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    @xmldata:SequenceOrder {value: 2}
    HttpMessageType httpMessage?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.http.sendHTTPRequest"}
public type ActivityErrorData record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption choiceOption;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.http.sendHTTPRequest"}
public type ActivityErrorDataType record {|
    @xmldata:Choice {minOccurs: 1, maxOccurs: 1}
    ChoiceOption choiceOption;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.http.sendHTTPRequest"}
public type ChoiceOption record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.http.sendHTTPRequest"}
    HttpClientException HttpClientException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.http.sendHTTPRequest"}
    HttpServerException HttpServerException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.http.sendHTTPRequest"}
    HttpCommunicationException HttpCommunicationException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.http.sendHTTPRequest"}
    ActivityTimedOutException ActivityTimedOutException?;
|};

@xmldata:Namespace {uri: "activity.jsonRender.output+b4f6a2ce-0fe1-42dd-b664-1220acad7966+ActivityOutputType"}
public type ActivityOutputClassType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup44 sequenceGroup44;
|};

@xmldata:Namespace {uri: "activity.jsonRender.output+b4f6a2ce-0fe1-42dd-b664-1220acad7966+ActivityOutputType"}
public type ActivityOutputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup44 sequenceGroup44;
|};

@xmldata:Namespace {uri: "activity.jsonRender.output+b4f6a2ce-0fe1-42dd-b664-1220acad7966+ActivityOutputType"}
public type SequenceGroup44 record {|
    @xmldata:Namespace {uri: "activity.jsonRender.output+b4f6a2ce-0fe1-42dd-b664-1220acad7966+ActivityOutputType"}
    @xmldata:SequenceOrder {value: 1}
    string jsonString;
|};

@xmldata:Name {value: "JSONActivityException"}
@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONActivityException1 record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup45 sequenceGroup45;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONActivityException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup45 sequenceGroup45;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type UnsupportedEncodingException record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup46 sequenceGroup46;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type UnsupportedEncodingExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup45 sequenceGroup45;
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup46 sequenceGroup46;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONParserExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup45 sequenceGroup45;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONParserException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONTransformExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup45 sequenceGroup45;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONTransformException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONRenderExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup45 sequenceGroup45;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONRenderException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONRestExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup45 sequenceGroup45;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONRestException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type ValidationExceptionType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup45 sequenceGroup45;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type ValidationException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type SequenceGroup45 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string msg;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    @xmldata:SequenceOrder {value: 2}
    string msgCode?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type SequenceGroup46 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    @xmldata:SequenceOrder {value: 1}
    string encoding;
|};

@xmldata:Namespace {uri: "activity.jsonParser.input+a3fa07a6-0270-48b7-ba84-7de6924acb3d+ActivityInputType"}
public type ActivityInputClassType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup47 sequenceGroup47;
|};

@xmldata:Namespace {uri: "activity.jsonParser.input+a3fa07a6-0270-48b7-ba84-7de6924acb3d+ActivityInputType"}
public type ActivityInputClass record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup47 sequenceGroup47;
|};

@xmldata:Namespace {uri: "activity.jsonParser.input+a3fa07a6-0270-48b7-ba84-7de6924acb3d+ActivityInputType"}
public type SequenceGroup47 record {|
    @xmldata:Namespace {uri: "activity.jsonParser.input+a3fa07a6-0270-48b7-ba84-7de6924acb3d+ActivityInputType"}
    @xmldata:SequenceOrder {value: 1}
    string jsonString;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.restjson.JsonRender"}
public type ChoiceOption1 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.restjson.JsonRender"}
    JSONRenderException JSONRenderException?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.restjson.JsonParser"}
public type ChoiceOption2 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+bw.restjson.JsonParser"}
    JSONParserException JSONParserException?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type SequenceGroup48 record {|
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
public type SequenceGroup49 record {|
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
public type SequenceGroup50 record {|
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
public type SequenceGroup51 record {|
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
public type SequenceGroup52 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 1}
    string \#content;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type SequenceGroup53 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 1}
    string msg;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 2}
    string msgCode?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type SequenceGroup54 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 1}
    string duplicateKey;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 2}
    string previousJobID?;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+44ece17e-f278-4255-b693-65bb9cf58bca"}
public type ChoiceOption3 record {|
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+44ece17e-f278-4255-b693-65bb9cf58bca"}
    ActivityTimedOutException ActivityTimedOutException?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+44ece17e-f278-4255-b693-65bb9cf58bca"}
    client4XXError client4XXError?;
    @xmldata:Namespace {uri: "http://tns.tibco.com/bw/palette/internal/activityerror+44ece17e-f278-4255-b693-65bb9cf58bca"}
    server5XXError server5XXError?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type SequenceGroup55 record {|
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
public type SequenceGroup56 record {|
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
public type SequenceGroup57 record {|
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
public type SequenceGroup58 record {|
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
public type SequenceGroup59 record {|
    @xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
    @xmldata:SequenceOrder {value: 1}
    string \#content;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type SequenceGroup60 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 1}
    string msg;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 2}
    string msgCode?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type SequenceGroup61 record {|
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 1}
    string duplicateKey;
    @xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    @xmldata:SequenceOrder {value: 2}
    string previousJobID?;
|};
