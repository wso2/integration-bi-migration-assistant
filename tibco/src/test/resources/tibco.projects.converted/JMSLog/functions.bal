import ballerina/lang.runtime;
import ballerina/log;
import ballerina/xslt;
import ballerinax/java.jms;

function JMG_Get(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    jms:Connection var1 = check new (initialContextFactory = "org.apache.activemq.jndi.ActiveMQInitialContextFactory", providerUrl = "tcp://localhost:61616");
    jms:Session var2 = check var1->createSession();
    //WARNING: using default destination configuration
    jms:MessageConsumer var3 = check var2.createConsumer(destination = {
        'type: jms:QUEUE,
        name: "Default queue"
    }
);
    jms:Message? var4 = check var3->receive();
    if var4 !is jms:TextMessage {
        return error("Unexpected msg type");
    }
    string var5 = var4.content;
    xml var6 = xml `<root>
       <ActivityOutput xmlns="http://www.tibco.com/namespaces/tnt/plugins/jms">
            <Body>
                ${var5}
            </Body>
       </ActivityOutput>
   </root>`;
    addToContext(cx, "JMG-Get", var6);
}

function JMS_Queue_Receiver(Context cx) returns error? {
    xml var0 = getFromContext(cx, "jms");
    xml var1 = xml `<root>
       <ActivityOutput xmlns="http://www.tibco.com/namespaces/tnt/plugins/jms">
            <Body>
                ${var0}
            </Body>
       </ActivityOutput>
   </root>`;
    addToContext(cx, "JMS-Queue-Receiver", var1);
}

function JMS_Send(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform1" match="/">
        <ns1:ActivityInput xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/jms">
                    
    <Body>
                            
        <xsl:value-of select="&quot;RECEIVED&quot;" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </Body>
                
</ns1:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    jms:Connection var2 = check new (initialContextFactory = "org.apache.activemq.jndi.ActiveMQInitialContextFactory", providerUrl = "tcp://localhost:61617", username = "userName", password = "password");
    jms:Session var3 = check var2->createSession();
    //WARNING: using default destination configuration
    jms:MessageProducer var4 = check var3.createProducer();
    string var5 = (var1/**/<Body>/*).toString().trim();
    jms:TextMessage var6 = {content: var5};
    check var4->send(var6);
    xml var7 = xml `<root></root>`;
    addToContext(cx, "JMS-Send", var7);
}

function Log(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="JMS-Queue-Receiver"/>     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema">
                    
    <message>
                            
        <xsl:value-of select="$JMS-Queue-Receiver/root/ns1:ActivityOutput/Body" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "Log", var2);
}

function Log_End(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0"><xsl:param name="JMS-Get"/>     <xsl:template name="Transform3" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema">
                    
    <message>
                            
        <xsl:value-of select="$JMS-Get/root/ns1:ActivityOutput/Body" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    xml var2 = var1/**/<message>/*;
    log:printInfo(var2.toString());
    addToContext(cx, "Log-End", var2);
}

function Sleep(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://xmlns.example.com" version="2.0">
     <xsl:template name="Transform2" match="/">
        <ns0:SleepInputSchema xmlns:ns0="http://www.tibco.com/namespaces/tnt/plugins/timer">
                    
    <IntervalInMillisec>
                            
        <xsl:value-of select="100" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </IntervalInMillisec>
                
</ns0:SleepInputSchema>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    decimal var2 = check decimal:fromString((var1/**/<IntervalInMillisec>/*).toString().trim());
    runtime:sleep(var2 / 1000);
    xml var3 = xml `<root></root>`;
    addToContext(cx, "Sleep", var3);
}

function scope0ActivityRunner(Context cx) returns error? {
    check JMS_Queue_Receiver(cx);
    check Log(cx);
    check JMS_Send(cx);
    check Sleep(cx);
}

function scope0FaultHandler(error err, Context cx) returns () {
    panic err;
}

function scope0ScopeFn(Context cx) returns () {
    error? result = scope0ActivityRunner(cx);
    if result is error {
        scope0FaultHandler(result, cx);
    }
}

function start_Main_process(Context cx) returns () {
    return scope0ScopeFn(cx);
}

function initContext(map<xml> initVariables = {}) returns Context {
    return {variables: initVariables, result: xml `<root/>`};
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
