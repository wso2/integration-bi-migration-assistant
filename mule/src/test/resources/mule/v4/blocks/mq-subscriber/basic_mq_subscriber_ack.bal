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
    remote function onMessage(jms:Message message) {
        Context ctx = {attributes: {jmsMessage: message}};
        log:printInfo("xxx: logger invoked");

        // TODO: UNSUPPORTED MULE BLOCK ENCOUNTERED. MANUAL CONVERSION REQUIRED.
        // ------------------------------------------------------------------------
        // <anypoint-mq:ack ackToken="#[vars.attributes.ackToken]" config-ref="mq_config" xmlns:doc="http://www.mulesoft.org/schema/mule/documentation" doc:id="7e3855d0-bb40-460c-999b-b4705f53198c" doc:name="Ack" xmlns:anypoint-mq="http://www.mulesoft.org/schema/mule/anypoint-mq">
        //         </anypoint-mq:ack>
        // ------------------------------------------------------------------------

    }
}
