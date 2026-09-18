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
    xml | error var4;
    do { jms:Session var5 = check var3->createSession();jms:MessageProducer var6 = check var5.createProducer(destination = {'type: jms:QUEUE, name: string `${SOAPRequestReplyEnvironment}.service.enterprise.utilities.logging.1.0.Request`});string var7 = uuid:createType4AsString();
// WARNING: TIBCO replies over a dynamically created temporary queue. Configure SOAPRequestReplyReplyToQueue with a queue the service can reply to
jms:MessageConsumer var8 = check var5.createConsumer(destination = {'type: jms:QUEUE, name: SOAPRequestReplyReplyToQueue}, messageSelector = string `JMSCorrelationID = '${var7}'`);xml var9 = xml`<soap:Envelope xmlns:soap="http://schemas.xmlsoap.org/soap/envelope/"
  soap:encodingStyle="http://schemas.xmlsoap.org/soap/encoding/">
    <soap:Header/>
    <soap:Body>
        ${var2}
    </soap:Body>
</soap:Envelope>`;jms:BytesMessage var10 = {deliveryMode: 2, priority: 4, correlationId: var7, replyTo: {'type: jms:QUEUE, name: SOAPRequestReplyReplyToQueue}, properties: {"SOAPAction": "/service/enterprise/utilities/logging/1.0/getconfiguration"}, content: var9.toString().toBytes()};check var6->send(var10);jms:Message? var11 = check var8->receive(SOAPRequestReplyTimeoutInSeconds * 1000);if var11 is () {
fail error("Timed out waiting for the SOAP response");
}string var12;if var11 is jms:TextMessage {
    var12 = var11.content;
} else if var11 is jms:BytesMessage {
    var12 = check string:fromBytes(var11.content);
} else {
    fail error("Unexpected SOAP response message type");
}
xml var13 = check xml:fromString(var12);var4 = var13/**/<soap:Body>/*; }on fail error var14 { var4 = var14; }
    check var3->close();
    xml var15 = check var4;
    xml var16 = xml`<root>${var15}</root>`;
    addToContext(cx, "SOAPRequestReply", var16);
}
