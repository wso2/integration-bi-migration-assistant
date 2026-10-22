import ballerina/log;
import ballerina/xslt;

function Log(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema" xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/jms" version="2.0">
     <xsl:template name="Transform0" match="/">
        <ns:ActivityInput xmlns:ns="http://www.tibco.com/pe/WriteToLogActivitySchema">
                    
    <message>
                            
        <xsl:value-of select="&quot;message received&quot;" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"/>
                        
    </message>
                
</ns:ActivityInput>

    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
    addToContext(cx, "Log", var3);
}

function Topic_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    // FIXME: Failed to convert rest of activity

    // <pd:starter name="Topic Receiver" xmlns:pd="http://xmlns.tibco.com/bw/process/2003">
    //     <pd:type>com.tibco.plugin.jms.JMSTopicEventSource</pd:type>
    //     <pd:resourceType>ae.activities.JMSTopicEventSource</pd:resourceType>
    //     <config>
    //         <PermittedMessageType>Text</PermittedMessageType>
    //         <SessionAttributes>
    //             <transacted>false</transacted>
    //             <acknowledgeMode>1</acknowledgeMode>
    //             <maxSessions>1</maxSessions>
    //             <destination>sample.topic</destination>
    //         </SessionAttributes>
    //         <ConfigurableHeaders>
    //             <JMSDeliveryMode>PERSISTENT</JMSDeliveryMode>
    //             <JMSExpiration>0</JMSExpiration>
    //             <JMSPriority>4</JMSPriority>
    //         </ConfigurableHeaders>
    //         <ConnectionReference>/UnsupportedStarter/SharedResources/jms.sharedjmscon</ConnectionReference>
    //     </config>
    //     <pd:inputBindings/>
    // </pd:starter>
    addToContext(cx, "Topic-Receiver", var0);
}

function scope0ActivityRunner(Context cx) returns error? {
    check Topic_Receiver(cx);
    check Log(cx);
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

function unsupported_Main_process_Topic_Receiver() {
    // FIXME: unsupported start activity: Topic Receiver

    // FIXME: replace this function with a listener and service that builds a Context via initContext and calls start_Main_process
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}

function initContext(map<xml> initVariables = {},
        map<SharedVariableContext> jobSharedVariables = {})
            returns Context {
    map<SharedVariableContext> sharedVariables = {};

    foreach var key in jobSharedVariables.keys() {
        sharedVariables[key] = jobSharedVariables.get(key);
    }
    return {variables: initVariables, result: xml `<root/>`, sharedVariables};
}
