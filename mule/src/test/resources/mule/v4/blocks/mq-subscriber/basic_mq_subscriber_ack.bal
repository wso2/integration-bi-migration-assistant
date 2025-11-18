import ballerina/log;
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
public listener jms:Listener mq_config = new jms:Listener(
    connectionConfig = {
        initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory",
        providerUrl: JMS_PROVIDER_URL
    },
    consumerOptions = {
        destination: {
            'type: jms:QUEUE,
            name: "test-queue"
        }
    }
);

// TODO: placeholder jms listener for mq_config
service "mq_config" on mq_config {
    remote function onMessage(jms:Message message, jms:Caller caller) returns error? {
        Context ctx = {attributes: {jmsMessage: message}};
        log:printInfo("xxx: logger invoked");
        check caller->acknowledge(<jms:Message>ctx.attributes.jmsMessage);
    }
}
