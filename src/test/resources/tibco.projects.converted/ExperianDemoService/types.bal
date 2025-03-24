import ballerina/data.xmldata;

type DuplicatedFieldNameException DuplicatedFieldNameExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type JSONParserExceptionType record {
    *JSONActivityException;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
type ActivityInputType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    int binaryContent?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    string asciiContent?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    headersType Headers?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    dynamicHeadersType DynamicHeaders?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    mimeEnvelopeElement anon8?;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type JSONActivityException record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
    string msgCode?;
};

type InvalidTimeZoneException InvalidTimeZoneExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
type X509CertificateType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
    int Encoded;
};

type ActivityInputClass ActivityInputClassType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input"}
type jdbcQueryActivityInput record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input"}
    string ssn;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input"}
    string ServerTimeZone?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input"}
    int timeout?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input"}
    int maxRows?;
};

type HttpClientException HttpClientExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/xml/5.0/JSONActivitiesExceptions"}
type JSONTransformExceptionType record {
    *JSONActivityException;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
type dynamicHeadersType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/REST"}
    dynamicHeadersTypeDetails[] Header;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
type ContextType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string RemoteAddress;
};

type ResponseActivityInput ActivityInputType;

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
type CertificateTokenType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
    string CipherSuite?;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
    CertificateChain anon3;
};

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
    string key?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    int processTimeout?;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
type headersType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    string StatusLine?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    string Content_Type?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    string Set_Cookie?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    string Pragma?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput"}
    string Location?;
};

type ActivityTimedOutException BaseExceptionType;

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
type mimeHeadersType record {|
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    string content_disposition?;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    string content_type;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    string content_transfer_encoding?;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    string content_id?;
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
    HttpMessageType httpMessage?;
};

type JSONRestException JSONRestExceptionType;

type ExperianResponseSchemaElement ExperianResponseSchemaElementType;

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
type OptionalErrorReport record {
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

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
type SecurityContextType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
    CertificateToken anon5?;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
    UsernamePasswordToken anon6?;
};

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
    string message?;
};

@xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
type InputElement record {
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
    string dob;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
    string firstName;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
    string lastName;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367"}
    string ssn;
};

type httpHeaders httpTransportHeaders;

type CorrelationValue string;

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
type mimeEnvelopeElementType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/encodings/mime"}
    mimePartType[] mimePart;
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
    string CustomJobId?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    string[] TrackingInfo;
};

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
type resultSet anydata;

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
    string MsgCode?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/pe/EngineTypes"}
    anydata Data?;
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
    int fiCOScore?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/json/1535671685533"}
    string rating?;
    @xmldata:Namespace {prefix: "tns", uri: "http://tns.tibco.com/bw/json/1535671685533"}
    int noOfInquiries?;
};

type JSONTransformException JSONTransformExceptionType;

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type JDBCConnectionNotFoundExceptionType record {
    *JDBCPluginExceptionType;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string jdbcConnection;
};

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
type CertificateType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
    string SubjectDN?;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
    string IssuerDN?;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
    int Fingerprint?;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
    X509Certificate anon4;
};

@xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
type HttpMessageType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    HeadersType headers;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    int binaryContent?;
    @xmldata:Namespace {prefix: "tns", uri: "http://schemas.tibco.com/bw/plugins/http/5.0/httpExceptions"}
    string asciiContent?;
};

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type LoginTimedOutExceptionType record {
    *JDBCPluginExceptionType;
};

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
type CertificateChainType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
    Certificate[] anon2;
};

type QueryData0 record {
    string ssn;
};

@xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
type HTTPEventSourceOutputType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string Method?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string RequestURI?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string HTTPVersion?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string PostData?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string QueryString?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string Header?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string Protocol?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    string Port?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    headersType Headers?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    dynamicHeadersType DynamicHeaders?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    mimeEnvelopeElement anon7?;
    @xmldata:Namespace {prefix: "tns", uri: "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType"}
    ContextType Context;
};

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
    string previousJobID?;
};

@xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
type UsernamePasswordTokenType record {
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
    string Username;
    @xmldata:Namespace {prefix: "tns", uri: "http://xmlns.tibco.com/bw/security/tokens"}
    string Password?;
};

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
};

type HttpServerException HttpServerExceptionType;

@xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
type ActivityExceptionType record {
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string msg;
    @xmldata:Namespace {prefix: "tns2", uri: "http://schemas.tibco.com/bw/plugins/jdbc/5.0/jdbcExceptions"}
    string msgCode?;
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
    string message?;
};

type ActivityErrorData ActivityErrorDataType;

type SecurityContext SecurityContextType;
