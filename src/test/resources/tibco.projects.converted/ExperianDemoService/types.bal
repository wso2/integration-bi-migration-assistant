type DuplicatedFieldNameException DuplicatedFieldNameExceptionType;

type JSONParserExceptionType record {
    *JSONActivityException;
};

type ActivityInputType record {|
    int binaryContent;
    string asciiContent;
    headersType Headers;
    dynamicHeadersType DynamicHeaders;
    anydata...;
|};

type JSONActivityException record {
    string msg;
    string msgCode;
};

type InvalidTimeZoneException InvalidTimeZoneExceptionType;

type X509CertificateType record {
    int Encoded;
};

type ActivityInputClass ActivityInputClassType;

type jdbcQueryActivityInput ();

type HttpClientException HttpClientExceptionType;

type JSONTransformExceptionType record {
    *JSONActivityException;
};

type dynamicHeadersType record {
    dynamicHeadersTypeDetails Header;
};

type ContextType record {
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

type FaultDetail record {
    string ActivityName;
    anydata Data;
    string Msg;
    string MsgCode;
    string ProcessStack;
    string StackTrace;
    string FullClass;
    string Class;
};

type httpResponseHeaders httpTransportResponseHeaders;

type tmessageBody string;

type HttpExceptionType record {
    *BaseExceptionType;
};

type client4XXError client4XXErrorType;

type WaitForHTTPRequestInputType record {
    string key;
    int processTimeout;
};

type headersType record {
    string StatusLine;
    string Content_Type;
    string Set_Cookie;
    string Pragma;
    string Location;
};

type ActivityTimedOutException BaseExceptionType;

type mimeHeadersType record {|
    string content_disposition;
    string content_type;
    string content_transfer_encoding;
    string content_id;
    anydata...;
|};

type mimePartType record {|
    mimeHeadersType mimeHeaders;
    int|string...;
|};

type ValidationExceptionType record {
    *JSONActivityException;
};

type HttpResponseExceptionType record {
    *BaseExceptionType;
    StatusLineType statusLine;
    HttpMessageType httpMessage;
};

type JSONRestException JSONRestExceptionType;

type ExperianResponseSchemaElement ExperianResponseSchemaElementType;

type OptionalErrorReport record {
    string StackTrace;
    string Msg;
    string FullClass;
    string Class;
    string ProcessStack;
    string MsgCode;
    anydata Data;
};

type HttpException HttpExceptionType;

type mimeEnvelopeElement mimeEnvelopeElementType;

type InvalidTimeZoneExceptionType record {
    *JDBCPluginExceptionType;
    string timeZone;
};

type JSONRestExceptionType record {
    *JSONActivityException;
};

type HttpServerExceptionType record {
    *HttpResponseExceptionType;
};

type server5XXError server5XXErrorType;

type UnsupportedEncodingExceptionType record {
    *JSONActivityException;
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

type DuplicatedFieldNameExceptionType record {
    *JDBCPluginExceptionType;
    string fieldName;
};

type httpFaultHeaders httpTransportFaultHeaders;

type JSONRenderExceptionType record {
    *JSONActivityException;
};

type httpTransportResponseHeaders record {
    string Content_Length;
    string Connection;
    string Pragma;
    string StatusLine;
    string Location;
    string Set_Cookie;
    string Content_Type;
    dynamicHeadersType DynamicHeaders;
};

type HttpClientExceptionType record {
    *HttpResponseExceptionType;
};

type CertificateToken CertificateTokenType;

type ActivityTimedOutExceptionType record {
    *ActivityExceptionType;
};

type server5XXErrorType record {
    int statusCode;
    string message;
};

type InputElement ();

type httpHeaders httpTransportHeaders;

type CorrelationValue string;

type mimeEnvelopeElementType record {
    mimePartType mimePart;
};

type ProcessContext record {
    string JobId;
    string ApplicationName;
    string EngineName;
    string ProcessInstanceId;
    string CustomJobId;
    string TrackingInfo;
};

type resultSet ();

type UsernamePasswordToken UsernamePasswordTokenType;

type HttpCommunicationExceptionType record {
    *BaseExceptionType;
};

type ErrorReport record {
    string StackTrace;
    string Msg;
    string FullClass;
    string Class;
    string ProcessStack;
    string MsgCode;
    anydata Data;
};

type BaseExceptionType record {
    string msg;
    string msgCode;
};

type statusLineType record {
    int statusCode;
};

type InvalidSQLTypeExceptionType record {
    *JDBCPluginExceptionType;
    string typeName;
};

type httpTransportFaultHeaders record {
    string Content_Length;
    string Connection;
    string Pragma;
    string StatusLine;
    string Location;
    string Set_Cookie;
    string Content_Type;
    dynamicHeadersType DynamicHeaders;
};

type dynamicHeadersTypeDetails record {
    string Name;
    string Value;
};

type messageBody tmessageBody;

type JDBCSQLExceptionType record {
    *JDBCPluginExceptionType;
    string sqlState;
    string detailStr;
};

type HttpCommunicationException HttpCommunicationExceptionType;

type ActivityException ActivityExceptionType;

type UnsupportedEncodingException UnsupportedEncodingExceptionType;

type JDBCPluginExceptionType record {
    *PluginExceptionType;
};

type ExperianResponseSchemaElementType record {
    int fiCOScore;
    string rating;
    int noOfInquiries;
};

type JSONTransformException JSONTransformExceptionType;

type JDBCConnectionNotFoundExceptionType record {
    *JDBCPluginExceptionType;
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

type HttpMessageType record {
    HeadersType headers;
    int binaryContent;
    string asciiContent;
};

type LoginTimedOutExceptionType record {
    *JDBCPluginExceptionType;
};

type CertificateChainType record {|
    anydata...;
|};

type QueryData0 record {
    string ssn;
};

type HTTPEventSourceOutputType record {|
    string Method;
    string RequestURI;
    string HTTPVersion;
    string PostData;
    string QueryString;
    string Header;
    string Protocol;
    string Port;
    headersType Headers;
    dynamicHeadersType DynamicHeaders;
    ContextType Context;
    anydata...;
|};

type StatusLineType record {
    string httpVersion;
    int statusCode;
    string reasonPhrase;
};

type ActivityOutputClassType record {
    string jsonString;
};

type Certificate CertificateType;

type JDBCConnectionNotFoundException JDBCConnectionNotFoundExceptionType;

type statusLine statusLineType;

type ValidationException ValidationExceptionType;

type JSONRenderException JSONRenderExceptionType;

type DuplicateKeyExceptionType record {
    *ActivityExceptionType;
    string duplicateKey;
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

type HeadersType record {|
    anydata...;
|};

type HttpResponseException HttpResponseExceptionType;

type ActivityInputClassType record {
    string jsonString;
};

type JDBCSQLException JDBCSQLExceptionType;

type ActivityErrorDataType JSONRenderException|();

type CertificateChain CertificateChainType;

type httpTransportHeaders record {
    string Accept;
    string Accept_Charset;
    string Accept_Encoding;
    string Content_Type;
    string Content_Length;
    string Connection;
    string Cookie;
    string Pragma;
    string Authorization;
    dynamicHeadersType DynamicHeaders;
};

type HttpServerException HttpServerExceptionType;

type ActivityExceptionType record {
    string msg;
    string msgCode;
};

type PluginExceptionType record {
    *ActivityExceptionType;
};

type ActivityOutputClass ActivityOutputClassType;

type DuplicateKeyException DuplicateKeyExceptionType;

type LoginTimedOutException LoginTimedOutExceptionType;

type client4XXErrorType record {
    int statusCode;
    string message;
};

type ActivityErrorData ActivityErrorDataType;

type SecurityContext SecurityContextType;
