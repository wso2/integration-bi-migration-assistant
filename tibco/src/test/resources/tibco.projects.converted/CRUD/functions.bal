import ballerina/data.xmldata;
import ballerina/log;
import ballerina/sql;
import ballerina/xslt;

function Catch(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(context, "Catch", var1);
    return var1;
}

function ErrorLog(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform2" match="/">
        <ActivityInput>
                    
    <message>
                            
        <xsl:value-of select="Error" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(context, "ErrorLog", var2);
    return var2;
}

function HTTP_Receiver(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(context, "HTTP-Receiver", var1);
    return var1;
}

function Log(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/EngineTypes">
                    
    <message>
         Request received for lat:
        <xsl:value-of select="$post//UserId" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(context, "Log", var2);
    return var2;
}

function SQL_Direct(map<xml> context) returns xml|error {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="post"/>     <xsl:template name="Transform1" match="/">
        <jdbcGeneralActivityInput>
                        
    <statement>
                                SELECT * FROM DB WHERE USER_ID=
        <xsl:value-of select="$post//UserId" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                            
    </statement>
                    
</jdbcGeneralActivityInput>

    </xsl:template>
</xsl:stylesheet>`, context);
    string var2 = (var1/**/<statement>/*).toString();
    sql:ParameterizedQuery var3 = `${var2}`;
    xml var4;
    if var2.startsWith("SELECT") {
        stream<map<anydata>, error|()> var5 = JDBCConnection->query(var3);
        xml var6 = xml ``;
        check from var each in var5
            do {
                xml var7 = check toXML(each);
                var6 = var6 + var7;
            };

        xml var8 = xml `<root>${var6}</root>`;
        var4 = var8;
    } else {
        sql:ExecutionResult var9 = check JDBCConnection->execute(var3);
        xml var10 = xml `<root></root>`;
        var4 = var10;
    }
    //WARNING: validate jdbc query result mapping
    addToContext(context, "SQL-Direct", var4);
    return var4;
}

function scope0ActivityRunner(map<xml> cx) returns xml|error {
    xml result0 = check HTTP_Receiver(cx);
    xml result1 = check Log(cx);
    xml result2 = check SQL_Direct(cx);
    return result2;
}

function scope0FaultHandler(error err, map<xml> cx) returns xml {
    xml result0 = checkpanic Catch(cx);
    xml result1 = checkpanic ErrorLog(cx);
    return result1;
}

function scope0ScopeFn(map<xml> cx) returns xml {
    xml|error result = scope0ActivityRunner(cx);
    if result is error {
        return scope0FaultHandler(result, cx);
    }
    return result;
}

function start_Processes_Main_process(xml inputXML, map<xml> params) returns xml {
    return scope0ScopeFn(params);
}

function toXML(map<anydata> data) returns error|xml {
    return xmldata:toXml(data);
}

function addToContext(map<xml> context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context[varName] = transformed;
}
