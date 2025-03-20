import ballerina/data.xmldata;
import ballerina/http;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type JSONParserExceptionType record {
    *JSONActivityException;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type JSONRenderExceptionType record {
    *JSONActivityException;
};

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

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type HttpClientExceptionType record {
    *HttpResponseExceptionType;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type JSONActivityException record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    string msgCode;
};

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

type creditscoreGetParameters ();

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
type RequestActivityOutput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Header;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    statusLineType statusLine;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    int binaryContent;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string asciiContent;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string filePath;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    outputHeadersType Headers;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    DynamicHeaders DynamicHeaders;
    anydata...;
|};

type InputElement ();

type httpHeaders httpTransportHeaders;

@xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
type GiveNewSchemaNameHere record {
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    string DOB;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    string FirstName;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    string LastName;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    string SSN;
};

type CorrelationValue string;

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
type mimeEnvelopeElementType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    mimePartType mimePart;
};

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
    string TrackingInfo;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type HttpCommunicationExceptionType record {
    *BaseExceptionType;
};

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

type getcreditdetailGetParameters ();

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type BaseExceptionType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string msgCode;
};

type ActivityInputClass ActivityInputClassType;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type statusLineType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    int statusCode;
};

type HttpClientException HttpClientExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type JSONTransformExceptionType record {
    *JSONActivityException;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
type RequestActivityInput record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Host;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    int Port;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Method;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string RequestURI;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string PostData;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string QueryString;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    int Timeout;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    inputHeadersType Headers;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    DynamicHeaders DynamicHeaders;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    parametersType parameters;
    anydata...;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type dynamicHeadersType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    dynamicHeadersTypeDetails Header;
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

type HttpCommunicationException HttpCommunicationExceptionType;

type HTTPRequestConfig record {
    string Method;
    string RequestURI;
    json PostData = "";
    map<string> Headers = {};
    map<string> parameters = {};
};

type ActivityException ActivityExceptionType;

type UnsupportedEncodingException UnsupportedEncodingExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.example.com/GetCreditDetail/headerParameters"}
type getcreditdetailGetHeaderType record {
    *httpTransportHeaders;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/json/1535671685533"}
type ExperianResponseSchemaElementType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/json/1535671685533"}
    int fiCOScore;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/json/1535671685533"}
    string rating;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/json/1535671685533"}
    int noOfInquiries;
};

type JSONTransformException JSONTransformExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
type outputHeadersType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Allow;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Content_Type;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Content_Length;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Content_Encoding;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Date;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Location;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Set_Cookie;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Pragma;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type HttpMessageType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    HeadersType headers;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    int binaryContent;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string asciiContent;
};

type JSONParserException JSONParserExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
type Header record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Name;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    string Value;
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

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
type inputHeadersType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Accept;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Accept_Charset;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Accept_Encoding;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Content_Type;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Cookie;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string Pragma;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
type DynamicHeaders record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput"}
    Header Header;
};

type httpResponseHeaders httpTransportResponseHeaders;

@xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
type SuccessSchema record {
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    int FICOScore;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    int NoOfInquiries;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    string Rating;
};

type tmessageBody string;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type StatusLineType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string httpVersion;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    int statusCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string reasonPhrase;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type HttpExceptionType record {
    *BaseExceptionType;
};

@xmldata:Namespace {prefix: "tns", uri: "activity.jsonRender.output+b4f6a2ce-0fe1-42dd-b664-1220acad7966+ActivityOutputType"}
type ActivityOutputClassType record {
    @xmldata:Namespace {prefix: "tns", uri: "activity.jsonRender.output+b4f6a2ce-0fe1-42dd-b664-1220acad7966+ActivityOutputType"}
    string jsonString;
};

type client4XXError client4XXErrorType;

type ActivityTimedOutException ActivityTimedOutExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
type mimeHeadersType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    string content_disposition;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    string content_type;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    string content_transfer_encoding;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    string content_id;
    anydata...;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
type mimePartType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    mimeHeadersType mimeHeaders;
    int|string...;
|};

type statusLine statusLineType;

type ValidationException ValidationExceptionType;

type JSONRenderException JSONRenderExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
type DuplicateKeyExceptionType record {
    *ActivityExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string duplicateKey;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string previousJobID;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type ValidationExceptionType record {
    *JSONActivityException;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type HttpResponseExceptionType record {
    *BaseExceptionType;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    StatusLineType statusLine;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    HttpMessageType httpMessage;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type HeadersType record {|
    anydata...;
|};

type HttpResponseException HttpResponseExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "activity.jsonParser.input+a3fa07a6-0270-48b7-ba84-7de6924acb3d+ActivityInputType"}
type ActivityInputClassType record {
    @xmldata:Namespace {prefix: "tns", uri: "activity.jsonParser.input+a3fa07a6-0270-48b7-ba84-7de6924acb3d+ActivityInputType"}
    string jsonString;
};

type JSONRestException JSONRestExceptionType;

type ExperianResponseSchemaElement ExperianResponseSchemaElementType;

type ActivityErrorDataType ActivityTimedOutException|()|http:NotFound|http:InternalServerError;

type creditscorePostParameters ();

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

type HttpException HttpExceptionType;

type mimeEnvelopeElement mimeEnvelopeElementType;

type HttpServerException HttpServerExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
type parametersType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput"}
    string ssn;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
type ActivityExceptionType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/pe/plugin/5.0/exceptions"}
    string msgCode;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type JSONRestExceptionType record {
    *JSONActivityException;
};

type ActivityOutputClass ActivityOutputClassType;

type getcreditdetailGetHeader getcreditdetailGetHeaderType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type HttpServerExceptionType record {
    *HttpResponseExceptionType;
};

type server5XXError server5XXErrorType;

type DuplicateKeyException DuplicateKeyExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
type CreditScoreSuccessSchema record {
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    SuccessSchema EquifaxResponse;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    SuccessSchema ExperianResponse;
    @xmldata:Namespace {prefix: "tns", uri: "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema"}
    SuccessSchema TransUnionResponse;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type UnsupportedEncodingExceptionType record {
    *JSONActivityException;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    string encoding;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type client4XXErrorType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    int statusCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string message;
};

type ActivityErrorData ActivityErrorDataType;

type httpFaultHeaders httpTransportFaultHeaders;

function getRequestPath(HTTPRequestConfig config) returns string {
    string base = config.RequestURI;
    if (config.parameters.length() == 0) {
        return base;
    }
    return base + "?" + "&".'join(...from string key in config.parameters.keys()
        select key + "=" + config.parameters.get(key));
}
