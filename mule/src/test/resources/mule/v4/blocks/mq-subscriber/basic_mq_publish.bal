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

public function mqSubscriberFlow() returns error?{
    Context ctx = {attributes: {}};
    jms:Connection connection = check new (
        connectionConfig = mq_configConfig
    );
    jms:Session session = check connection->createSession();
    jms:MessageProducer producer = check session.createProducer({
        'type: jms:QUEUE,
        name: "destinationQueue"
    });
    jms:MapMessage message = <jms:MapMessage>check _dwMethod(ctx);
    check producer->send(message);
    return;
}

function _dwMethod(Context ctx) returns anydata|error {
    return {
        "foo": "bar"
    };
}
