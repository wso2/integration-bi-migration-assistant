import ballerina/data.xmldata;

public type ExperianResponseSchemaElement anydata;

public type QueryData0 record {|
    string ssn;
|};

public type InputElement anydata;

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

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
public type InputElement record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup8 sequenceGroup8;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
public type SequenceGroup8 record {|
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
SequenceGroup9 sequenceGroup9;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/json/1535671685533"}
public type ExperianResponseSchemaElement record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup9 sequenceGroup9;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/json/1535671685533"}
public type SequenceGroup9 record {|
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

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
public type InputElement record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup8 sequenceGroup8;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
public type SequenceGroup8 record {|
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
SequenceGroup9 sequenceGroup9;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/json/1535671685533"}
public type ExperianResponseSchemaElement record {|
@xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
SequenceGroup9 sequenceGroup9;
|};

@xmldata:Namespace {uri: "http://tns.tibco.com/bw/json/1535671685533"}
public type SequenceGroup9 record {|
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
