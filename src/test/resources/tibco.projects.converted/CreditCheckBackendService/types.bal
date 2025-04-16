import ballerina/data.xmldata;

public type Response anydata;

public type Element anydata;

public type Request anydata;

public type QueryData1 record {|
    int noOfPulls;
    string ssn;
|};

public type QueryData0 record {|
    string ssn;
|};

public type httpHeaders anydata;

@xmldata:Name {
    value: "Record"
}
public type QueryResult0 record {|
    string firstname;
    string lastname;
    string ssn;
    string dateofBirth;
    int ficoscore;
    string rating;
    int numofpulls;
|};

public type LogParametersType anydata;
 import ballerina/data.xmldata;

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ErrorReport record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type OptionalErrorReport record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type FaultDetail record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type 'anydata record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup4 sequenceGroup4;
|};

@xmldata:Name {value: "OptionalErrorReport"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type OptionalErrorReport1 record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup1 sequenceGroup1;
|};

@xmldata:Name {value: "ErrorReport"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ErrorReport1 record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup sequenceGroup;
|};

@xmldata:Name {value: "FaultDetail"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type FaultDetail1 record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup2 sequenceGroup2;
|};

@xmldata:Name {value: "ProcessContext"}
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type ProcessContext1 record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type CorrelationValue record {|
string \#content;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
public type SequenceGroup record {|
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
public type SequenceGroup1 record {|
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
public type SequenceGroup2 record {|
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
public type SequenceGroup3 record {|
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
public type SequenceGroup4 record {|
@xmldata:Namespace {uri: "http://www.tibco.com/pe/EngineTypes"}
@xmldata:SequenceOrder {value: 1}
string \#content;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityExceptionType record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityException record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityTimedOutExceptionType record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityTimedOutException record {|
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type DuplicateKeyExceptionType record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup5 sequenceGroup5;
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup6 sequenceGroup6;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type DuplicateKeyException record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup6 sequenceGroup6;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type SequenceGroup5 record {|
@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
@xmldata:SequenceOrder {value: 1}
string msg;
@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
@xmldata:SequenceOrder {value: 2}
string msgCode?;
|};

@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type SequenceGroup6 record {|
@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
@xmldata:SequenceOrder {value: 1}
string duplicateKey;
@xmldata:Namespace {uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
@xmldata:SequenceOrder {value: 2}
string previousJobID?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
public type LogParametersType record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
public type ActivityInput record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
public type SequenceGroup7 record {|
@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
@xmldata:SequenceOrder {value: 1}
string msgCode?;
@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
@xmldata:SequenceOrder {value: 2}
string loggerName?;
@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
@xmldata:SequenceOrder {value: 3}
string logLevel?;
@xmldata:Namespace {uri: "http://www.tibco.com/pe/WriteToLogActivitySchema"}
@xmldata:SequenceOrder {value: 4}
string message;
|};
import ballerina/data.xmldata;

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type messageBody record {|
string \#content;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type tmessageBody string;

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

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type httpTransportFaultHeaders record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type dynamicHeadersTypeDetails record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup3 sequenceGroup3;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type dynamicHeadersType record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup4 sequenceGroup4;
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
public type httpFaultHeaders record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type statusLineType record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type statusLine record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup5 sequenceGroup5;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type client4XXErrorType record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup6 sequenceGroup6;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type client4XXError record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup6 sequenceGroup6;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type server5XXErrorType record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type server5XXError record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup7 sequenceGroup7;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type SequenceGroup record {|
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
public type SequenceGroup1 record {|
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

@xmldata:Namespace {uri: "http://www.example.com/namespaces/tns/1535845694732"}
public type Element record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup8 sequenceGroup8;
|};

@xmldata:Namespace {uri: "http://www.example.com/namespaces/tns/1535845694732"}
public type SequenceGroup8 record {|
@xmldata:Namespace {uri: "http://www.example.com/namespaces/tns/1535845694732"}
@xmldata:SequenceOrder {value: 1}
string ssn;
|};

@xmldata:Namespace {uri: "http://www.example.com/namespaces/tns/1535845694732"}
public type SequenceGroup9 record {|
@xmldata:Namespace {uri: "http://www.example.com/namespaces/tns/1535845694732"}
@xmldata:SequenceOrder {value: 1}
string ssn;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/REST"}
public type tmessageBody1 string;

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

@xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
public type Response record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup18 sequenceGroup18;
|};

@xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
public type SuccessSchema record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup18 sequenceGroup18;
|};

@xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
public type Request record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup19 sequenceGroup19;
|};

@xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
public type RequestType record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup19 sequenceGroup19;
|};

@xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
public type SequenceGroup18 record {|
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
public type SequenceGroup19 record {|
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

@xmldata:Namespace {uri: "/T1535753828744Converted/JsonSchema"}
public type SequenceGroup20 record {|
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
public type SequenceGroup21 record {|
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
