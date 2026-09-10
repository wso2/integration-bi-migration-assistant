function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "QueryRecords-input");
    xml var1 = check xml:fromString(string`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input" xmlns:tns1="http://www.example.com/namespaces/tns/1535845694732" version="2.0"><xsl:param name="Start"/><xsl:template name="Template" match="/"><tns:jdbcQueryActivityInput><col1><xsl:value-of select="$Start/root/col1"/></col1></tns:jdbcQueryActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string col1 = (var2/<col1>/*).toString().trim();
    sql:ParameterizedQuery var3 = `select * from t where col1 like ${col1}`;
    stream<record{|anydata...;|}, error?> var4 = jdbcProperty->query(var3);
    xml var5 = xml``;
    check from var each in var4 do {
    xml var6 = check toXML(each);
    var5 = var5 + xml `<Record>${var6}</Record>`;
};

    xml var7 = xml`<root>${var5}</root>`;
    addToContext(cx, "QueryRecords", var7);
}
