import ballerinax/java.jms;

public type Attributes record {|
    map<string> uriParams = {};
    jms:Message jmsMessage?;
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

configurable string JMS_PROVIDER_URL = ?;
jms:ConnectionConfiguration mq_configConfig = {initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory", providerUrl: JMS_PROVIDER_URL};
public listener jms:Listener mq_config = new jms:Listener(
    connectionConfig = mq_configConfig,
    consumerOptions = {
        destination: {
            'type: jms:QUEUE,
            name: "test-queue"
        }
    }
);

// TODO: placeholder jms listener for mq_config
service "mq_config" on mq_config {
    jms:MessageProducer producer0;
    jms:MessageProducer producer1;

    function init() returns error? {
        jms:Connection connection0 = check new (mq_configConfig);
        jms:Session session0 = check connection0->createSession();
        self.producer0 = check session0.createProducer({'type: jms:QUEUE, name: "destinationQueue"});
        jms:Connection connection1 = check new (mq_configConfig);
        jms:Session session1 = check connection1->createSession();
        self.producer1 = check session1.createProducer({'type: jms:QUEUE, name: "destinationQueue2"});
    }

    remote function onMessage(jms:Message message, jms:Caller caller) returns error? {
        Context ctx = {attributes: {jmsMessage: message}};
        jms:MapMessage jmsMessage0 = {
            content: {
                "foo": "bar"
            }
        };
        check self.producer0->send(jmsMessage0);
        jms:MapMessage jmsMessage1 = {
            content: {
                "baz": "bar"
            }
        };
        check self.producer1->send(jmsMessage1);
    }
}
