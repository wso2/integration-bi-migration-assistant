import ballerina/log;
import ballerinax/java.jms;

configurable string JMS_PROVIDER_URL = ?;
jms:ConnectionConfiguration mq_configConfig = {initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory", providerUrl: JMS_PROVIDER_URL};
public listener jms:Listener mq_config = new jms:Listener(
    connectionConfig = mq_configConfig,
    consumerOptions = {
        destination: {
            'type: jms:QUEUE,
            name: "orders-queue"
        }
    }
);

// TODO: placeholder jms listener for mq_config
service "mq_config" on mq_config {
    function init() returns error? {
    }

    remote function onMessage(jms:Message message, jms:Caller caller) returns error? {
        Context ctx = {attributes: {jmsMessage: message}};
        log:printInfo("orders-queue message received");
    }
}
