type DuplicatedFieldNameException DuplicatedFieldNameExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type JSONParserExceptionType record {
    *JSONActivityException;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
type ActivityInputType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    int binaryContent;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    string asciiContent;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    headersType Headers;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    dynamicHeadersType DynamicHeaders;
    anydata...;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type JSONActivityException record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    string msgCode;
};

type InvalidTimeZoneException InvalidTimeZoneExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
type X509CertificateType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
    int Encoded;
};

type ActivityInputClass ActivityInputClassType;

type jdbcQueryActivityInput ();

type HttpClientException HttpClientExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type JSONTransformExceptionType record {
    *JSONActivityException;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type dynamicHeadersType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    dynamicHeadersTypeDetails Header;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
type ContextType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string RemoteAddress;
};

type ResponseActivityInput ActivityInputType;

//FIXME: Failed to convert type due to [ParseError] : Element name is empty and minOccurs is not 0
//<element minOccurs="1" ref="tns:CertificateChain" xmlns="http://www.w3.org/2001/XMLSchema"/>

//<complexType name="CertificateTokenType" xmlns="http://www.w3.org/2001/XMLSchema">
//    <sequence>
//        <element minOccurs="0" name="CipherSuite" type="string"/>
//        <element minOccurs="1" ref="tns:CertificateChain"/>
//    </sequence>
//</complexType>
type CertificateTokenType anydata;

type X509Certificate X509CertificateType;

type ProcessStarterOutput HTTPEventSourceOutputType;

type InvalidSQLTypeException InvalidSQLTypeExceptionType;

type input WaitForHTTPRequestInputType;

type JSONParserException JSONParserExceptionType;

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

type httpResponseHeaders httpTransportResponseHeaders;

type tmessageBody string;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type HttpExceptionType record {
    *BaseExceptionType;
};

type client4XXError client4XXErrorType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
type WaitForHTTPRequestInputType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string key;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    int processTimeout;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
type headersType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    string StatusLine;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    string Content_Type;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    string Set_Cookie;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    string Pragma;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    string Location;
};

type ActivityTimedOutException BaseExceptionType;

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

type JSONRestException JSONRestExceptionType;

type ExperianResponseSchemaElement ExperianResponseSchemaElementType;

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

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type InvalidTimeZoneExceptionType record {
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string timeZone;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type JSONRestExceptionType record {
    *JSONActivityException;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type HttpServerExceptionType record {
    *HttpResponseExceptionType;
};

type server5XXError server5XXErrorType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type UnsupportedEncodingExceptionType record {
    *JSONActivityException;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    string encoding;
};

type JDBCPluginException JDBCPluginExceptionType;

//FIXME: Failed to convert type due to [ParseError] : Unsupported complex type body tag: all
//<complexType name="SecurityContextType" xmlns="http://www.w3.org/2001/XMLSchema">
//    <all>
//        <element maxOccurs="1" minOccurs="0" ref="tns:CertificateToken"/>
//        <element maxOccurs="1" minOccurs="0" ref="tns:UsernamePasswordToken"/>
//    </all>
//</complexType>

//<complexType name="SecurityContextType" xmlns="http://www.w3.org/2001/XMLSchema">
//    <all>
//        <element maxOccurs="1" minOccurs="0" ref="tns:CertificateToken"/>
//        <element maxOccurs="1" minOccurs="0" ref="tns:UsernamePasswordToken"/>
//    </all>
//</complexType>
type SecurityContextType anydata;

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type DuplicatedFieldNameExceptionType record {
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string fieldName;
};

type httpFaultHeaders httpTransportFaultHeaders;

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

type CertificateToken CertificateTokenType;

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

type InputElement ();

type httpHeaders httpTransportHeaders;

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

type resultSet ();

type UsernamePasswordToken UsernamePasswordTokenType;

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

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type BaseExceptionType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string msgCode;
};

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

type HttpCommunicationException HttpCommunicationExceptionType;

type ActivityException ActivityExceptionType;

type UnsupportedEncodingException UnsupportedEncodingExceptionType;

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type JDBCPluginExceptionType record {
    *PluginExceptionType;
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

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type JDBCConnectionNotFoundExceptionType record {
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string jdbcConnection;
};

//FIXME: Failed to convert type due to [ParseError] : Element name is empty and minOccurs is not 0
//<element minOccurs="1" ref="tns:X509Certificate" xmlns="http://www.w3.org/2001/XMLSchema"/>

//<complexType name="CertificateType" xmlns="http://www.w3.org/2001/XMLSchema">
//    <sequence>
//        <element minOccurs="0" name="SubjectDN" type="string"/>
//        <element minOccurs="0" name="IssuerDN" type="string"/>
//        <element minOccurs="0" name="Fingerprint" type="base64Binary"/>
//        <element minOccurs="1" ref="tns:X509Certificate"/>
//    </sequence>
//</complexType>
type CertificateType anydata;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type HttpMessageType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    HeadersType headers;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    int binaryContent;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string asciiContent;
};

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type LoginTimedOutExceptionType record {
    *JDBCPluginExceptionType;
};

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
type CertificateChainType record {|
    anydata...;
|};

type QueryData0 record {
    string ssn;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
type HTTPEventSourceOutputType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string Method;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string RequestURI;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string HTTPVersion;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string PostData;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string QueryString;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string Header;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string Protocol;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string Port;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    headersType Headers;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    dynamicHeadersType DynamicHeaders;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    ContextType Context;
    anydata...;
|};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type StatusLineType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string httpVersion;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    int statusCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string reasonPhrase;
};

@xmldata:Namespace {prefix: "tns", uri: "activity.jsonRender.output+8ccea717-63a9-4d35-945d-ec9437e37100+ActivityOutputType"}
type ActivityOutputClassType record {
    @xmldata:Namespace {prefix: "tns", uri: "activity.jsonRender.output+8ccea717-63a9-4d35-945d-ec9437e37100+ActivityOutputType"}
    string jsonString;
};

type Certificate CertificateType;

type JDBCConnectionNotFoundException JDBCConnectionNotFoundExceptionType;

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

//FIXME: Failed to convert type due to Name cannot be null or empty

//<complexType name="UsernamePasswordTokenType" xmlns="http://www.w3.org/2001/XMLSchema">
//    <sequence>
//        <element maxOccurs="1" minOccurs="1" name="Username" type="string"/>
//        <element minOccurs="0" name="Password">
//            <complexType>
//                <simpleContent>
//                    <extension base="string">
//                        <attribute name="type" type="string"/>
//                    </extension>
//                </simpleContent>
//            </complexType>
//        </element>
//    </sequence>
//</complexType>
type UsernamePasswordTokenType anydata;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type HeadersType record {|
    anydata...;
|};

type HttpResponseException HttpResponseExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "activity.jsonParser.input+f396d921-0bc7-459d-ae81-0eb1a0f94723+ActivityInputType"}
type ActivityInputClassType record {
    @xmldata:Namespace {prefix: "tns", uri: "activity.jsonParser.input+f396d921-0bc7-459d-ae81-0eb1a0f94723+ActivityInputType"}
    string jsonString;
};

type JDBCSQLException JDBCSQLExceptionType;

type ActivityErrorDataType JSONRenderException|();

type CertificateChain CertificateChainType;

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

type HttpServerException HttpServerExceptionType;

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type ActivityExceptionType record {
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string msgCode;
};

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type PluginExceptionType record {
    *ActivityExceptionType;
};

type ActivityOutputClass ActivityOutputClassType;

type DuplicateKeyException DuplicateKeyExceptionType;

type LoginTimedOutException LoginTimedOutExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type client4XXErrorType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    int statusCode;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    string message;
};

type ActivityErrorData ActivityErrorDataType;

type SecurityContext SecurityContextType;
