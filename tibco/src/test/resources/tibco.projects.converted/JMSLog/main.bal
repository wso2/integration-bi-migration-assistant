import ballerinax/java.jms;

public listener jms:Listener jmsJMS_Queue_ReceiverListener = new jms:Listener(
    connectionConfig = {
        initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory",
        providerUrl: "tcp://localhost:61616"
    },
    consumerOptions = {
        destination: {
            'type: jms:QUEUE,
            name: "queue"
        }
    }
);

service "JMS_Queue_Receiver" on jmsJMS_Queue_ReceiverListener {
    remote function onMessage(jms:Message message) {
        map<SharedVariableContext> jobSharedVariables = {};
        if message !is jms:TextMessage {
            panic error("Unsupported JMS message type");
        }
        string content = message.content;
        xml inputXML = xml `${content}`;
        map<xml> paramXML = {jms: inputXML};
        Context cx = initContext(paramXML, jobSharedVariables);
        start_Main_process(cx);
    }
}

xmlns "http://www.tibco.com/namespaces/tnt/plugins/timer" as ns0;
xmlns "http://xmlns.tibco.com/bw/process/2003" as pd;
xmlns "http://www.w3.org/1999/XSL/Transform" as xsl;
xmlns "http://www.tibco.com/namespaces/tnt/plugins/jms" as ns1;
xmlns "http://www.tibco.com/pe/WriteToLogActivitySchema" as ns;
