function activityExtension(Context context) returns xml | error {
    xml var0 = context.get("QueryRecords-input");
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input" xmlns:tns1="http://www.example.com/namespaces/tns/1535845694732" version="2.0"><xsl:param name="Start"/><xsl:template name="Template" match="/"><tns:jdbcQueryActivityInput><col1><xsl:value-of select="$Start/root/col1"/></col1></tns:jdbcQueryActivityInput></xsl:template></xsl:stylesheet>`, context);
    string col1 = (var1/<col1>/*).toString().trim();
    sql:ParameterizedQuery var2 = `select * from t where col1 like ${col1}`;
    stream<map<anydata>, error | ()> var3 = jdbcProperty->query(var2);
    xml var4 = xml``;
    check from var each in var3 do {
    xml var5 = check toXML(each);
    var4 = var4 + xml `<Record>${var5}</Record>`;
};

    xml var6 = xml`<root>${var4}</root>`;
    addToContext(context, "QueryRecords", var6);
    return var6;
}
