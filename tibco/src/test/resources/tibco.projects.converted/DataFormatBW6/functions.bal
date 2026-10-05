import ballerina/io;
import ballerina/xslt;

function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ParseOrders-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/parse/xsd/+ParseOrders+input" version="2.0"><xsl:template name="ParseOrders-input" match="/"><tns1:Input><fileName><xsl:value-of select="'/data/inbound/orders.txt'"/></fileName><startRecord><xsl:value-of select="1"/></startRecord><noOfRecords><xsl:value-of select="-1"/></noOfRecords></tns1:Input></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = check string:fromBytes(check io:fileReadBytes((var2/**/<fileName>/*).toString().trim()));
    int var4 = check int:fromString((var2/**/<startRecord>/*).toString().trim());
    int var5 = check int:fromString((var2/**/<noOfRecords>/*).toString().trim());
    string var6 = (var2/**/<SkipHeaderCharacters>/*).toString().trim();
    int var7 = var6 == "" ? 0 : check int:fromString(var6);
    xml var8 = check parseDelimitedData(var3, ";", "\n", "http://example.com/xsd/orders", "order", ["orderId", "customer", "amount"], true, var4, var5, var7, false);
    addToContext(cx, "ParseOrders", var8);
}

function activityExtension_1(Context cx) returns error? {
    xml var0 = getFromContext(cx, "RenderReport-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/render/xsd/+RenderReport+input" xmlns:tns2="http://example.com/xsd/orders" version="2.0"><xsl:param name="ParseOrders"/><xsl:template name="RenderReport-input" match="/"><tns1:Rows><xsl:for-each select="$ParseOrders/root/Rows/tns2:order"><tns2:order><tns2:orderId><xsl:value-of select="tns2:orderId"/></tns2:orderId><tns2:customer><xsl:value-of select="upper-case(tns2:customer)"/></tns2:customer><tns2:amount><xsl:value-of select="tns2:amount"/></tns2:amount></tns2:order></xsl:for-each></tns1:Rows></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = renderDelimitedData(var2, "order", ";", "\n", ["orderId", "customer", "amount"]);
    xml var4 = xml `<root>${var3}</root>`;
    addToContext(cx, "RenderReport", var4);
}

function activityExtension_2(Context cx) returns error? {
    xml var0 = getFromContext(cx, "WriteReport-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://www.tibco.com/namespaces/tnt/plugins/file" version="2.0"><xsl:param name="RenderReport"/><xsl:template name="WriteReport-input" match="/"><tns3:WriteActivityInputTextClass><fileName><xsl:value-of select="'/data/outbound/orders-report.txt'"/></fileName><textContent><xsl:value-of select="$RenderReport"/></textContent></tns3:WriteActivityInputTextClass></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string fileName = (var2/**/<fileName>/*).toString();
    string content = (var2/**/<textContent>/*).toString();
    check io:fileWriteString(fileName, content, "OVERWRITE");
}

function receiveEvent(Context cx) returns error? {
}

function scopeActivityRunner(Context cx) returns error? {
    check receiveEvent(cx);
    check activityExtension(cx);
    check activityExtension_1(cx);
    check activityExtension_2(cx);
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

function start_test_dataformat_OrderReportProcess(Context params) returns () {
    scopeScopeFn(params);
}

function splitBySeparator(string text, string separator) returns string[] {
    string[] parts = [];
    int startIndex = 0;
    int? separatorIndex = text.indexOf(separator, startIndex);
    while separatorIndex is int {
        parts.push(text.substring(startIndex, separatorIndex));
        startIndex = separatorIndex + separator.length();
        separatorIndex = text.indexOf(separator, startIndex);
    }
    parts.push(text.substring(startIndex));
    return parts;
}

// TIBCO's "New Line" separator also accepts Windows line endings, so a trailing carriage return is
// dropped from each line.
function dataFormatLines(string content, string lineSeparator, int skipHeaderCharacters)
        returns string[] {
    string text = skipHeaderCharacters >= content.length() ? ""
        : content.substring(skipHeaderCharacters);
    if text.endsWith(lineSeparator) {
        text = text.substring(0, text.length() - lineSeparator.length());
    }
    if text == "" {
        return [];
    }
    return from string line in splitBySeparator(text, lineSeparator)
        select lineSeparator == "\n" && line.endsWith("\r")
            ? line.substring(0, line.length() - 1) : line;
}

function hasMoreDataFormatRecords(string[] lines, int fromIndex, boolean skipBlankLines)
        returns boolean {
    foreach int index in fromIndex ..< lines.length() {
        if lines[index].trim() != "" {
            return true;
        }
        if !skipBlankLines {
            return false;
        }
    }
    return false;
}

// Mirrors TIBCO Parse Data: startRecord is a 1-based line number, a negative noOfRecords
// reads every record, and a blank line ends the input unless blank lines are skipped.
// Field elements share the row's namespace only when the schema qualifies local elements.
function parseDelimitedData(string content, string columnSeparator, string lineSeparator,
        string namespace, string rowName, string[] fieldNames, boolean qualifiedFields,
        int startRecord, int noOfRecords, int skipHeaderCharacters, boolean skipBlankLines)
        returns xml|error {
    string[] lines = dataFormatLines(content, lineSeparator, skipHeaderCharacters);
    string prefix = namespace == "" ? "" : "{" + namespace + "}";
    string fieldPrefix = qualifiedFields ? prefix : "";
    // A prefixed declaration keeps unqualified fields out of the row's namespace when serialized,
    // since a default-namespace declaration would be inherited by them.
    map<string> rowAttributes = namespace == "" ? {}
        : {"{http://www.w3.org/2000/xmlns/}ns0": namespace};
    xml rows = xml ``;
    int recordCount = 0;
    int lineIndex = startRecord < 1 ? 0 : startRecord - 1;
    boolean reachedBlankLine = false;
    while lineIndex < lines.length() && (noOfRecords < 0 || recordCount < noOfRecords) {
        string line = lines[lineIndex];
        lineIndex += 1;
        if line.trim() == "" {
            if skipBlankLines {
                continue;
            }
            reachedBlankLine = true;
            break;
        }
        string[] values = splitBySeparator(line, columnSeparator);
        if values.length() > fieldNames.length() {
            return error(string `BadDataFormatException: line ${lineIndex} has ${values.length()} `
                + string `fields, expected at most ${fieldNames.length()}`);
        }
        xml fields = xml ``;
        foreach int index in 0 ..< fieldNames.length() {
            string value = index < values.length() ? values[index] : "";
            fields += xml:createElement(fieldPrefix + fieldNames[index], {}, xml:createText(value));
        }
        rows += xml:createElement(prefix + rowName, rowAttributes, fields);
        recordCount += 1;
    }
    boolean done = reachedBlankLine || !hasMoreDataFormatRecords(lines, lineIndex, skipBlankLines);
    return xml `<root><Rows>${rows}</Rows><done>${done}</done></root>`;
}

// The rows come out of an XSLT mapping, so they are matched by local name regardless of
// the namespace the mapping put them in.
function renderDelimitedData(xml input, string rowName, string columnSeparator,
        string lineSeparator, string[] fieldNames) returns string {
    string[] lines = from xml:Element row in (input/**/<*>).elements()
        where localXmlName(row) == rowName
        select string:'join(columnSeparator, ...from string fieldName in fieldNames
                    select dataFormatFieldValue(row, fieldName));
    return string:'join(lineSeparator, ...lines);
}

function dataFormatFieldValue(xml:Element row, string fieldName) returns string {
    foreach xml:Element child in row.children().elements() {
        if localXmlName(child) == fieldName {
            return child.data();
        }
    }
    return "";
}

function localXmlName(xml:Element element) returns string {
    string name = element.getName();
    int? namespaceEnd = name.lastIndexOf("}");
    return namespaceEnd is int ? name.substring(namespaceEnd + 1) : name;
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
