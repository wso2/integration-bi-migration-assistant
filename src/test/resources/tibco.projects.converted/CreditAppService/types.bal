import ballerina/data.xmldata;
import ballerina/http;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONParserExceptionType record {|
    *JSONActivityException;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONRenderExceptionType record {|
    *JSONActivityException;
|};

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

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpClientExceptionType record {|
    *HttpResponseExceptionType;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONActivityException record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    string msgCode?;
|};

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

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.example.com/Creditscore/parameters"}
public type creditscoreGetParameters record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.example.com/Creditscore/parameters"}
    string DOB?;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.example.com/Creditscore/parameters"}
    string FirstName?;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.example.com/Creditscore/parameters"}
    string LastName?;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.example.com/Creditscore/parameters"}
    string SSN?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
public type RequestActivityOutput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Header?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    statusLineType statusLine?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    int binaryContent?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string asciiContent?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string filePath?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    outputHeadersType Headers?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    DynamicHeaders DynamicHeaders?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    mimeEnvelopeElement anon1?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
public type InputElement record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
    string dob;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
    string firstName;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
    string lastName;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
    string ssn;
|};

public type httpHeaders httpTransportHeaders;

@xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
public type GiveNewSchemaNameHere record {|
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    string DOB?;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    string FirstName?;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    string LastName?;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    string SSN?;
|};

public type CorrelationValue string;

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
public type mimeEnvelopeElementType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    mimePartType[] mimePart;
|};

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

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpCommunicationExceptionType record {|
    *BaseExceptionType;
|};

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

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.example.com/GetCreditDetail/parameters"}
public type getcreditdetailGetParameters record {|
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type BaseExceptionType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string msgCode;
|};

public type ActivityInputClass ActivityInputClassType;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
public type statusLineType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    int statusCode;
|};

public type HttpClientException HttpClientExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONTransformExceptionType record {|
    *JSONActivityException;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type RequestActivityInput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Host?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    int Port?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Method?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string RequestURI?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string PostData?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string QueryString?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    int Timeout?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    inputHeadersType Headers?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    DynamicHeaders DynamicHeaders?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    parametersType parameters?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    mimeEnvelopeElement anon0?;
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

public type HttpCommunicationException HttpCommunicationExceptionType;

public type HTTPRequestConfig record {|
    string Method;
    string RequestURI;
    json PostData = "";
    map<string> Headers = {};
    map<string> parameters = {};
|};

public type ActivityException ActivityExceptionType;

public type UnsupportedEncodingException UnsupportedEncodingExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.example.com/GetCreditDetail/headerParameters"}
public type getcreditdetailGetHeaderType record {|
    *httpTransportHeaders;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/json/1535671685533"}
public type ExperianResponseSchemaElementType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/json/1535671685533"}
    int fiCOScore?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/json/1535671685533"}
    string rating?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/json/1535671685533"}
    int noOfInquiries?;
|};

public type JSONTransformException JSONTransformExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
public type outputHeadersType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Allow?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Content_Type?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Content_Length?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Content_Encoding?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Date?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Location?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string[] Set_Cookie;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Pragma?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpMessageType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    HeadersType headers;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    int binaryContent?;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string asciiContent?;
|};

public type JSONParserException JSONParserExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
public type Header record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Name;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Value;
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

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type inputHeadersType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Accept?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Accept_Charset?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Accept_Encoding?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Content_Type?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Cookie?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Pragma?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
public type DynamicHeaders record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    Header[] Header;
|};

public type httpResponseHeaders httpTransportResponseHeaders;

@xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
public type SuccessSchema record {|
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    int FICOScore?;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    int NoOfInquiries?;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    string Rating?;
|};

public type tmessageBody string;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type StatusLineType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string httpVersion;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    int statusCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string reasonPhrase;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpExceptionType record {|
    *BaseExceptionType;
|};

@xmldata:Namespace {prefix: "tns", uri: "activity.jsonRender.output+b4f6a2ce-0fe1-42dd-b664-1220acad7966+ActivityOutputType"}
public type ActivityOutputClassType record {|
    @xmldata:Namespace {prefix: "tns", uri: "activity.jsonRender.output+b4f6a2ce-0fe1-42dd-b664-1220acad7966+ActivityOutputType"}
    string jsonString;
|};

public type client4XXError client4XXErrorType;

public type ActivityTimedOutException ActivityTimedOutExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
public type mimeHeadersType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    string content_disposition?;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    string content_type;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    string content_transfer_encoding?;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    string content_id?;
};

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
public type mimePartType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    mimeHeadersType mimeHeaders;
...int|string
|};

public type statusLine statusLineType;

public type ValidationException ValidationExceptionType;

public type JSONRenderException JSONRenderExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type DuplicateKeyExceptionType record {|
    *ActivityExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string duplicateKey;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string previousJobID?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type ValidationExceptionType record {|
    *JSONActivityException;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpResponseExceptionType record {|
    *BaseExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    StatusLineType statusLine;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    HttpMessageType httpMessage?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HeadersType record {
};

public type HttpResponseException HttpResponseExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "activity.jsonParser.input+a3fa07a6-0270-48b7-ba84-7de6924acb3d+ActivityInputType"}
public type ActivityInputClassType record {|
    @xmldata:Namespace {prefix: "tns", uri: "activity.jsonParser.input+a3fa07a6-0270-48b7-ba84-7de6924acb3d+ActivityInputType"}
    string jsonString;
|};

public type JSONRestException JSONRestExceptionType;

public type ExperianResponseSchemaElement ExperianResponseSchemaElementType;

public type ActivityErrorDataType ActivityTimedOutException|()|http:NotFound|http:InternalServerError;

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.example.com/Creditscore/parameters"}
public type creditscorePostParameters record {|
|};

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

public type HttpException HttpExceptionType;

public type mimeEnvelopeElement mimeEnvelopeElementType;

public type HttpServerException HttpServerExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
public type parametersType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string ssn?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
public type ActivityExceptionType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string msgCode?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type JSONRestExceptionType record {|
    *JSONActivityException;
|};

public type ActivityOutputClass ActivityOutputClassType;

public type getcreditdetailGetHeader getcreditdetailGetHeaderType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
public type HttpServerExceptionType record {|
    *HttpResponseExceptionType;
|};

public type server5XXError server5XXErrorType;

public type DuplicateKeyException DuplicateKeyExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
public type CreditScoreSuccessSchema record {|
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    SuccessSchema EquifaxResponse?;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    SuccessSchema ExperianResponse?;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    SuccessSchema TransUnionResponse?;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
public type UnsupportedEncodingExceptionType record {|
    *JSONActivityException;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    string encoding;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
public type client4XXErrorType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    int statusCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string message?;
|};

public type ActivityErrorData ActivityErrorDataType;

public type httpFaultHeaders httpTransportFaultHeaders;

function getRequestPath(HTTPRequestConfig config) returns string {
    string base = config.RequestURI;
    if (config.parameters.length() == 0) {
        return base;
    }
    return base + "?" + "&".'join(...from string key in config.parameters.keys()
        select key + "=" + config.parameters.get(key));
}

function getRequestPath(HTTPRequestConfig config) returns string {
    string base = config.RequestURI;
    if (config.parameters.length() == 0) {
        return base;
    }
    return base + "?" + "&".'join(...from string key in config.parameters.keys()
        select key + "=" + config.parameters.get(key));
}
