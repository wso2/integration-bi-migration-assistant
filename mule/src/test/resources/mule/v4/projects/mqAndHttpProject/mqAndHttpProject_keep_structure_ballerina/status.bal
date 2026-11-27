import ballerina/http;
import ballerina/log;
import ballerinax/java.jms;

jms:ConnectionConfiguration status_mq_configConfig = {initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory", providerUrl: JMS_PROVIDER_URL};
public listener jms:Listener status_mq_config = new jms:Listener(
    connectionConfig = status_mq_configConfig,
    consumerOptions = {
        destination: {
            'type: jms:QUEUE,
            name: "status-queue"
        }
    }
);

service / on listener_config {
    function init() returns error? {
    }

    resource function default status(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};

        // set payload
        string payload1 = "status ok";
        ctx.payload = payload1;

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

// TODO: placeholder jms listener for status_mq_config
service "status_mq_config" on status_mq_config {
    function init() returns error? {
    }

    remote function onMessage(jms:Message message, jms:Caller caller) returns error? {
        Context ctx = {attributes: {jmsMessage: message}};
        log:printInfo("status-queue message received");
    }
}
