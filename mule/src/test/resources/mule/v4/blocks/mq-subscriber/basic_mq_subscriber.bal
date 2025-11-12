import ballerina/log;
import ballerinax/java.jms;

public type Attributes record {|
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

configurable string JMS_PROVIDER_URL = ?;

service mq_config on new jms:Listener(
    connectionConfig = {
        initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory",
        providerUrl: JMS_PROVIDER_URL
    }
) {
    remote function onMessage(jms:Message message) {
        log:printInfo("xxx: logger invoked");
    }
}
