public type JSONParserExceptionType record {
    *JSONActivityException;
};

public type JSONRenderExceptionType record {
    *JSONActivityException;
};

public type HttpClientExceptionType record {
    *HttpResponseExceptionType;
};

public type JSONActivityException record {
    string msg;
    string msgCode;
};

public type ActivityTimedOutExceptionType record {
    *ActivityExceptionType;
};

public type RequestActivityOutput record {|
    string Header;
    statusLineType statusLine;
    int binaryContent;
    string asciiContent;
    string filePath;
    outputHeadersType Headers;
    DynamicHeaders DynamicHeaders;
    anydata...;
|};

public type GiveNewSchemaNameHere record {
    string DOB;
    string FirstName;
    string LastName;
    string SSN;
};

public type CorrelationValue string;

public type mimeEnvelopeElementType record {
    mimePartType mimePart;
};

public type ProcessContext record {
    string JobId;
    string ApplicationName;
    string EngineName;
    string ProcessInstanceId;
    string CustomJobId;
    string TrackingInfo;
};

public type HttpCommunicationExceptionType record {
    *BaseExceptionType;
};

public type ErrorReport record {
    string StackTrace;
    string Msg;
    string FullClass;
    string Class;
    string ProcessStack;
    string MsgCode;
    anydata Data;
};

public type BaseExceptionType record {
    string msg;
    string msgCode;
};

public type ActivityInputClass ActivityInputClassType;

public type statusLineType record {
    string httpVersion;
    int statusCode;
    string reasonPhrase;
};

public type HttpClientException HttpClientExceptionType;

public type JSONTransformExceptionType record {
    *JSONActivityException;
};

public type RequestActivityInput record {|
    string Host;
    int Port;
    string Method;
    string RequestURI;
    string PostData;
    string QueryString;
    int Timeout;
    inputHeadersType Headers;
    DynamicHeaders DynamicHeaders;
    parametersType parameters;
    anydata...;
|};

public type HttpCommunicationException HttpCommunicationExceptionType;

public type HTTPRequestConfig record {
    string Method;
    string RequestURI;
    json PostData = "";
    map<string> Headers = {};
    map<string> parameters = {};
};

public type ActivityException ActivityExceptionType;

public type UnsupportedEncodingException UnsupportedEncodingExceptionType;

public type ExperianResponseSchemaElementType record {
    int fiCOScore;
    string rating;
    int noOfInquiries;
};

public type JSONTransformException JSONTransformExceptionType;

public type outputHeadersType record {
    string Allow;
    string Content_Type;
    string Content_Length;
    string Content_Encoding;
    string Date;
    string Location;
    string Set_Cookie;
    string Pragma;
};

public type HttpMessageType record {
    HeadersType headers;
    int binaryContent;
    string asciiContent;
};

public type JSONParserException JSONParserExceptionType;

public type Header record {
    string Name;
    string Value;
};

public type FaultDetail record {
    string ActivityName;
    anydata Data;
    string Msg;
    string MsgCode;
    string ProcessStack;
    string StackTrace;
    string FullClass;
    string Class;
};

public type inputHeadersType record {
    string Accept;
    string Accept_Charset;
    string Accept_Encoding;
    string Content_Type;
    string Cookie;
    string Pragma;
};

public type DynamicHeaders record {
    Header Header;
};

public type SuccessSchema record {
    int FICOScore;
    int NoOfInquiries;
    string Rating;
};

public type StatusLineType record {
    string httpVersion;
    int statusCode;
    string reasonPhrase;
};

public type HttpExceptionType record {
    *BaseExceptionType;
};

public type ActivityOutputClassType record {
    string jsonString;
};

public type ActivityTimedOutException BaseExceptionType;

public type mimeHeadersType record {|
    string content_disposition;
    string content_public type;
    string content_transfer_encoding;
    string content_id;
    anydata...;
|};

public type mimePartType record {|
    mimeHeadersType mimeHeaders;
    int|string...;
|};

public type ValidationException ValidationExceptionType;

public type JSONRenderException JSONRenderExceptionType;

public type DuplicateKeyExceptionType record {
    *ActivityExceptionType;
    string duplicateKey;
    string previousJobID;
};

public type ValidationExceptionType record {
    *JSONActivityException;
};

public type HttpResponseExceptionType record {
    *BaseExceptionType;
    StatusLineType statusLine;
    HttpMessageType httpMessage;
};

public type HeadersType record {|
    anydata...;
|};

public type HttpResponseException HttpResponseExceptionType;

public type ActivityInputClassType record {
    string jsonString;
};

public type JSONRestException JSONRestExceptionType;

public type ExperianResponseSchemaElement ExperianResponseSchemaElementType;

public type ActivityErrorDataType JSONParserException|();

public type OptionalErrorReport record {
    string StackTrace;
    string Msg;
    string FullClass;
    string Class;
    string ProcessStack;
    string MsgCode;
    anydata Data;
};

public type HttpException HttpExceptionType;

public type mimeEnvelopeElement mimeEnvelopeElementType;

public type HttpServerException HttpServerExceptionType;

public type parametersType record {
    string ssn;
};

public type ActivityExceptionType record {
    string msg;
    string msgCode;
};

public type JSONRestExceptionType record {
    *JSONActivityException;
};

public type ActivityOutputClass ActivityOutputClassType;

public type HttpServerExceptionType record {
    *HttpResponseExceptionType;
};

public type DuplicateKeyException DuplicateKeyExceptionType;

public type CreditScoreSuccessSchema record {
    SuccessSchema EquifaxResponse;
    SuccessSchema ExperianResponse;
    SuccessSchema TransUnionResponse;
};

public type UnsupportedEncodingExceptionType record {
    *JSONActivityException;
    string encoding;
};

public type ActivityErrorData ActivityErrorDataType;

function getRequestPath(HTTPRequestConfig config) returns string {
    string base = config.RequestURI;
    if (config.parameters.length() == 0) {
        return base;
    }
    return base + "?" + "&".'join(...from string key in config.parameters.keys()
        select key + "=" + config.parameters.get(key));
}
