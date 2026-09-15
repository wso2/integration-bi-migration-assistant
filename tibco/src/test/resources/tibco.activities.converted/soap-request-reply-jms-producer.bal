function SOAPRequestReply(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0">
     <xsl:template name="Transform0" match="/">
        <inputMessage>
                
    <message>
                "foo bar"
            </message>
            
</inputMessage>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xmlns "http://schemas.xmlsoap.org/soap/envelope/" as soap;
    jms:Connection var3 = check new (initialContextFactory = "com.tibco.tibjms.naming.TibjmsInitialContextFactory", providerUrl = "tibjmsnaming://localhost:7222", connectionFactoryName = "QueueConnectionFactory", username = "admin", properties = {"java.naming.security.principal": "admin"});
    jms:Session var4 = check var3->createSession();
    jms:MessageProducer var5 = check var4.createProducer(destination = {'type: jms:QUEUE, name: "%%Environment%%.service.enterprise.utilities.logging.1.0.Request"});
    string var6 = uuid:createType4AsString();
    
// WARNING: TIBCO replies over a dynamically created temporary queue. Configure SOAPRequestReplyReplyToQueue with a queue the service can reply to

    jms:MessageConsumer var7 = check var4.createConsumer(destination = {'type: jms:QUEUE, name: SOAPRequestReplyReplyToQueue}, messageSelector = string `JMSCorrelationID = '${var6}'`);
    xml var8 = xml`<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
  soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <soap:Header/>
    <soap:Body>
        ${var2}
    </soap:Body>
</soap:Envelope>`;
    jms:BytesMessage var9 = {deliveryMode: 2, priority: 4, correlationId: var6, replyTo: {'type: jms:QUEUE, name: SOAPRequestReplyReplyToQueue}, properties: {"SOAPAction": "/service/enterprise/utilities/logging/1.0/getconfiguration"}, content: var8.toString().toBytes()};
    check var5->send(var9);
    jms:Message? var10 = check var7->receive(SOAPRequestReplyTimeoutInSeconds * 1000);
    if var10 is () {
return error("Timed out waiting for the SOAP response");
}
    string var11;
    if var10 is jms:TextMessage {
    var11 = var10.content;
} else if var10 is jms:BytesMessage {
    var11 = check string:fromBytes(var10.content);
} else {
    return error("Unexpected SOAP response message type");
}

    xml var12 = check xml:fromString(var11);
    xml var13 = var12/**/<soap:Body>/*;
    xml var14 = xml`<root>${var13}</root>`;
    addToContext(cx, "SOAPRequestReply", var14);
}
