function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "InputVariable");
    xml var1 = check xslt:transform(var0, xml`<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns6="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" xmlns:tns="http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367" version="2.0">
    <xsl:param name="Start"/>
    <xsl:template name="RenderJSON-input" match="/">
        <tns:InputElement>
            <tns:dob>
                <xsl:value-of select="$Start/root/tns6:DOB"/>
            </tns:dob>
            <tns:firstName>
                <xsl:value-of select="$Start/root/tns6:FirstName"/>
            </tns:firstName>
            <tns:lastName>
                <xsl:value-of select="$Start/root/tns6:LastName"/>
            </tns:lastName>
            <tns:ssn>
                <xsl:value-of select="$Start/root/tns6:SSN"/>
            </tns:ssn>
        </tns:InputElement>
    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    
//WARNING: assuming single element

    InputElement var2 = check xmldata:parseAsType(var1);
    string var3 = var2.toJsonString();
    xml var4 = xml`<jsonString>${var3}</jsonString>`;
    addToContext(cx, "OutputVariable", var4);
}
