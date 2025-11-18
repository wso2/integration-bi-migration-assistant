import ballerinax/java.jms;

public type Context record {|
    anydata payload = ();
|};

configurable string JMS_PROVIDER_URL = ?;
jms:ConnectionConfiguration mq_configConfig = {initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory", providerUrl: JMS_PROVIDER_URL};

public function mqSubscriberFlow(Context ctx) {
    jms:Connection connection0 = check new (mq_configConfig);
    jms:Session session0 = check connection0->createSession();
    jms:MessageProducer producer0 = check session0.createProducer({'type: jms:QUEUE, name: "destinationQueue"});
    jms:MapMessage jmsMessage0 = {
        content: {
            "foo": "bar"
        }
    };
    check producer0->send(jmsMessage0);
}
