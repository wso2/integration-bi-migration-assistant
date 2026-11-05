import ballerina/time;
import ballerina/xslt;
import ballerinax/sap.jco;

function CreateSalesOrders(Context cx) returns error? {
    xml var0 = getFromContext(cx, "CreateSalesOrders-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://www.tibco.com/xmlns/sap/Sales.SAPConnection/Z_CREATE_ORDERS" version="2.0"><xsl:template name="CreateSalesOrders-input" match="/"><tns1:RequestInputType><RfcRequest><I_MODE><xsl:value-of select="'S'"/></I_MODE><I_HEADER><SALES_ORG><xsl:value-of select="'1000'"/></SALES_ORG><CHANNEL><xsl:value-of select="'WEB'"/></CHANNEL></I_HEADER><I_ORDERS><item><ORDER_ID><xsl:value-of select="'ORD-1'"/></ORDER_ID><QUANTITY><xsl:value-of select="5"/></QUANTITY></item><item><ORDER_ID><xsl:value-of select="'ORD-2'"/></ORDER_ID><QUANTITY><xsl:value-of select="2"/></QUANTITY></item></I_ORDERS><I_EMPTY/><CS_STATUS><STATUS><xsl:value-of select="'NEW'"/></STATUS></CS_STATUS></RfcRequest></tns1:RequestInputType></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    jco:RfcParameters var3 = sapRfcParameters(var2, ["T_MESSAGES"], ["I_ORDERS", "I_EMPTY"]);
    jco:RfcRecord var4 = check Sales_SAPConnection->execute("Z_CREATE_ORDERS", var3);
    xml var5 = xml `<root><RfcResponse>${sapRfcToXml(var4)}</RfcResponse></root>`;
    addToContext(cx, "CreateSalesOrders", var5);
}

function LookupBalance(Context cx) returns error? {
    xml var0 = getFromContext(cx, "LookupBalance-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://www.tibco.com/xmlns/sap/Finance.SAPConnection/Z_GET_BALANCE" version="2.0"><xsl:param name="CreateSalesOrders"/><xsl:template name="LookupBalance-input" match="/"><tns1:RequestInputType><RfcRequest><I_COMPANY><xsl:value-of select="$CreateSalesOrders/root/RfcResponse/E_COMPANY"/></I_COMPANY></RfcRequest></tns1:RequestInputType></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    jco:RfcParameters var3 = sapRfcParameters(var2, [], []);
    jco:RfcRecord var4 = check Finance_SAPConnection->execute("Z_GET_BALANCE", var3);
    xml var5 = xml `<root><RfcResponse>${sapRfcToXml(var4)}</RfcResponse></root>`;
    addToContext(cx, "LookupBalance", var5);
}

function PostTransactional(Context cx) returns error? {
    // FIXME: SAP Invoke RFC BAPI is only supported for non-transactional Request/Reply invocations

    // <bpws:extensionActivity xmlns:bpws="http://docs.oasis-open.org/wsbpel/2.0/process/executable">
    //     <tibex:activityExtension xmlns:tibex="http://www.tibco.com/bpel/2007/extensions" inputVariable="PostTransactional-input" name="PostTransactional" outputVariable="PostTransactional">
    //         <bpws:targets>
    //             <bpws:target linkName="LookupBalanceToPostTransactional"/>
    //         </bpws:targets>
    //         <tibex:inputBindings>
    //             <tibex:inputBinding expression="&lt;?xml version=&quot;1.0&quot; encoding=&quot;UTF-8&quot;?&gt;&#10;&lt;xsl:stylesheet xmlns:xsl=&quot;http://www.w3.org/1999/XSL/Transform&quot; xmlns:tns1=&quot;http://www.tibco.com/xmlns/sap/Finance.SAPConnection/Z_GET_BALANCE&quot; version=&quot;2.0&quot;&gt;&lt;xsl:template name=&quot;PostTransactional-input&quot; match=&quot;/&quot;&gt;&lt;tns1:RequestInputType&gt;&lt;RfcRequest&gt;&lt;I_COMPANY&gt;&lt;xsl:value-of select=&quot;'1000'&quot;/&gt;&lt;/I_COMPANY&gt;&lt;/RfcRequest&gt;&lt;/tns1:RequestInputType&gt;&lt;/xsl:template&gt;&lt;/xsl:stylesheet&gt;" expressionLanguage="urn:oasis:names:tc:wsbpel:2.0:sublang:xslt1.0"/>
    //         </tibex:inputBindings>
    //         <tibex:config>
    //             <bwext:BWActivity xmlns:activityconfig="http://tns.tibco.com/bw/model/activityconfig" xmlns:bwext="http://tns.tibco.com/bw/model/core/bwext" xmlns:internalization="http://ns.tibco.com/bw/palette/sap/internalization" xmlns:sap="http://ns.tibco.com/bw/palette/sap" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance" activityTypeID="bw.sap.InvokeRequestResponse">
    //                 <activityConfig>
    //                     <properties name="config" xsi:type="activityconfig:EMFProperty">
    //                         <type href="http://ns.tibco.com/bw/palette/sap#//InvokeRequestResponse"/>
    //                         <value connectionReference="FinanceSAPConnection" operation="Z_GET_BALANCE" schemaPath="/SapBW6/Schemas/.SAP/Finance.SAPConnection/functionModules/Z_GET_BALANCE.xsd" transactional="true" xsi:type="sap:InvokeRequestResponse"/>
    //                     </properties>
    //                 </activityConfig>
    //             </bwext:BWActivity>
    //         </tibex:config>
    //     </tibex:activityExtension>
    // </bpws:extensionActivity>
}

function Start(Context cx) returns error? {
}

function scopeActivityRunner(Context cx) returns error? {
    check PostTransactional(cx);
    check Start(cx);
    check CreateSalesOrders(cx);
    check LookupBalance(cx);
}

function scopeFaultHandler(error err, Context cx) returns () {
    panic err;
}

function scopeScopeFn(Context cx) returns () {
    error? result = scopeActivityRunner(cx);
    if result is error {
        scopeFaultHandler(result, cx);
    }
}

function start_test_sap_OrderSyncProcess(Context params) returns () {
    scopeScopeFn(params);
}

// importTableParameterNames is () when the RFC schema was not available; the top-level
// parameters are then classified from the XML shape alone.
function sapRfcParameters(xml request, string[] tableParameterNames,
        string[]? importTableParameterNames) returns jco:RfcParameters {
    jco:RfcRecord importParameters = {};
    map<jco:RfcRecord[]> tableParameters = {};
    foreach xml:Element param in (request/**/<RfcRequest>)/<*> {
        string name = param.getName();
        if tableParameterNames.indexOf(name) != () {
            tableParameters[name] = sapRfcRows(param);
        } else if importTableParameterNames is () {
            importParameters[name] = sapRfcValue(param);
        } else if importTableParameterNames.indexOf(name) != () {
            importParameters[name] = sapRfcRows(param);
        } else {
            importParameters[name] = (param/<*>).length() == 0 ? param.data()
                : sapRfcStructure(param);
        }
    }
    return {importParameters, tableParameters};
}

// Below the top level (or without a schema) a value made only of <item> children is taken to be
// table rows.
function sapRfcValue(xml:Element element) returns jco:FieldType {
    xml children = element/<*>;
    if children.length() == 0 {
        return element.data();
    }
    if (element/<item>).length() == children.length() {
        return sapRfcRows(element);
    }
    return sapRfcStructure(element);
}

function sapRfcRows(xml:Element 'table) returns jco:RfcRecord[] {
    jco:RfcRecord[] rows = [];
    foreach xml:Element row in 'table/<item> {
        rows.push(sapRfcStructure(row));
    }
    return rows;
}

function sapRfcStructure(xml:Element structure) returns jco:RfcRecord {
    jco:RfcRecord fields = {};
    foreach xml:Element 'field in structure/<*> {
        fields['field.getName()] = sapRfcValue('field);
    }
    return fields;
}

function sapRfcToXml(jco:RfcRecord fields) returns xml {
    xml result = xml ``;
    foreach [string, jco:FieldType?] [name, value] in fields.entries() {
        result += xml:createElement(name, {}, sapRfcValueToXml(value));
    }
    return result;
}

function sapRfcValueToXml(jco:FieldType? value) returns xml {
    if value is () {
        return xml ``;
    }
    if value is time:Date {
        return xml:createText(string `${sapPad(value.year, 4)}-${sapPad(value.month, 2)}-${
                sapPad(value.day, 2)}`);
    }
    if value is time:TimeOfDay {
        // Fractional seconds are truncated so that e.g. 59.6 never becomes 60.
        int second = <int>(value.second ?: 0d).floor();
        return xml:createText(string `${sapPad(value.hour, 2)}:${sapPad(value.minute, 2)}:${
                sapPad(second, 2)}`);
    }
    if value is byte[] {
        return xml:createText(value.toBase64());
    }
    if value is record {|jco:FieldType?...;|}[] {
        xml rows = xml ``;
        foreach record {|jco:FieldType?...;|} row in value {
            rows += xml:createElement("item", {}, sapRfcToXml(row));
        }
        return rows;
    }
    if value is record {|jco:FieldType?...;|} {
        return sapRfcToXml(value);
    }
    return xml:createText(value.toString());
}

function sapPad(int value, int width) returns string {
    string digits = value.toString();
    while digits.length() < width {
        digits = "0" + digits;
    }
    return digits;
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}

function getFromContext(Context context, string varName) returns xml {
    xml? value = context.variables[varName];
    if value == () {
        return xml `<root/>`;
    }
    return value;
}
