import ballerina/data.xmldata;

public type Context record {|
    map<xml> variables;
    xml result;
    Response response?;
    map<SharedVariableContext> sharedVariables;
|};

public type JSONResponse readonly & record {|
    *Response;
    "JSONResponse" kind = "JSONResponse";
    json payload;
|};

public type Response record {|
    "JSONResponse"|"XMLResponse"|"TextResponse" kind;
    anydata payload;
    map<string> headers;
|};

public type SharedVariableContext record {|
    function () returns xml getter;
    function (xml value) setter;
|};

public type TextResponse readonly & record {|
    *Response;
    "TextResponse" kind = "TextResponse";
    string payload;
|};

public type XMLResponse readonly & record {|
    *Response;
    "XMLResponse" kind = "XMLResponse";
    xml payload;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type RequestInputType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type RequestInputTypeParam record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup sequenceGroup;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type RfcRequest record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type Z_CREATE_ORDERS_RequestParam record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup1 sequenceGroup1;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type RequestOutputType record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type RequestOutputTypeParam record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup2 sequenceGroup2;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type RfcResponse record {|
    @xmldata:Sequence {minOccurs: 0, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type Z_CREATE_ORDERS_ResponseParam record {|
    @xmldata:Sequence {minOccurs: 0, maxOccurs: 1}
    SequenceGroup3 sequenceGroup3?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type ORDER_HEADER record {|
    @xmldata:Sequence {minOccurs: 0, maxOccurs: 1}
    SequenceGroup4 sequenceGroup4?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type ORDER_STATUS record {|
    @xmldata:Sequence {minOccurs: 0, maxOccurs: 1}
    SequenceGroup5 sequenceGroup5?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type sequence_lsqb_ORDER_LINE_rsqb_ record {|
    @xmldata:Sequence {minOccurs: 0, maxOccurs: 65535}
    SequenceGroup6[] sequenceGroup6?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type ORDER_LINE record {|
    @xmldata:Sequence {minOccurs: 0, maxOccurs: 1}
    SequenceGroup7 sequenceGroup7?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type sequence_lsqb_ORDER_MESSAGE_rsqb_ record {|
    @xmldata:Sequence {minOccurs: 0, maxOccurs: 65535}
    SequenceGroup8[] sequenceGroup8?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS"}
public type ORDER_MESSAGE record {|
    @xmldata:Sequence {minOccurs: 0, maxOccurs: 1}
    SequenceGroup9 sequenceGroup9?;
|};

public type SequenceGroup record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string sessionID?;
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    int timeout?;
    @xmldata:SequenceOrder {value: 3}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    Z_CREATE_ORDERS_RequestParam RfcRequest;
|};

public type SequenceGroup1 record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string I_MODE?;
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    ORDER_HEADER I_HEADER?;
    @xmldata:SequenceOrder {value: 3}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    sequence_lsqb_ORDER_LINE_rsqb_ I_ORDERS;
    @xmldata:SequenceOrder {value: 4}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    sequence_lsqb_ORDER_LINE_rsqb_ I_EMPTY?;
    @xmldata:SequenceOrder {value: 5}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    ORDER_STATUS CS_STATUS?;
    @xmldata:SequenceOrder {value: 6}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    sequence_lsqb_ORDER_MESSAGE_rsqb_ T_MESSAGES?;
|};

public type SequenceGroup2 record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string sessionID?;
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string CPIC_ID?;
    @xmldata:SequenceOrder {value: 3}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    Z_CREATE_ORDERS_ResponseParam RfcResponse;
|};

public type SequenceGroup3 record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string E_COMPANY?;
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    ORDER_STATUS CS_STATUS?;
    @xmldata:SequenceOrder {value: 3}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    sequence_lsqb_ORDER_MESSAGE_rsqb_ T_MESSAGES?;
|};

public type SequenceGroup4 record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string SALES_ORG?;
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string CHANNEL?;
|};

public type SequenceGroup5 record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string STATUS?;
|};

public type SequenceGroup6 record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 65535}
    ORDER_LINE[] item?;
|};

public type SequenceGroup7 record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string ORDER_ID?;
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string QUANTITY?;
|};

public type SequenceGroup8 record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 65535}
    ORDER_MESSAGE[] item?;
|};

public type SequenceGroup9 record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string TYPE?;
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string TEXT?;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Finance.SAPConnection/Z_GET_BALANCE"}
public type Z_GET_BALANCE_RequestParam record {|
    @xmldata:Sequence {minOccurs: 1, maxOccurs: 1}
    SequenceGroup11 sequenceGroup11;
|};

@xmldata:Namespace {uri: "http://www.tibco.com/xmlns/sap/Finance.SAPConnection/Z_GET_BALANCE"}
public type Z_GET_BALANCE_ResponseParam record {|
    @xmldata:Sequence {minOccurs: 0, maxOccurs: 1}
    SequenceGroup12 sequenceGroup12?;
|};

public type SequenceGroup10 record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string sessionID?;
    @xmldata:SequenceOrder {value: 2}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    int timeout?;
    @xmldata:SequenceOrder {value: 3}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    Z_GET_BALANCE_RequestParam RfcRequest;
|};

public type SequenceGroup11 record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
    string I_COMPANY;
|};

public type SequenceGroup12 record {|
    @xmldata:SequenceOrder {value: 1}
    @xmldata:Element {minOccurs: 0, maxOccurs: 1}
    string E_BALANCE?;
|};
