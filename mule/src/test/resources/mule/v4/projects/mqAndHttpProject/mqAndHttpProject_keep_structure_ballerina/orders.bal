import ballerina/http;
import ballerina/log;
import ballerinax/java.jms;

configurable string JMS_PROVIDER_URL = ?;
jms:ConnectionConfiguration orders_mq_configConfig = {initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory", providerUrl: JMS_PROVIDER_URL};
public listener jms:Listener orders_mq_config = new jms:Listener(
    connectionConfig = orders_mq_configConfig,
    consumerOptions = {
        destination: {
            'type: jms:QUEUE,
            name: "orders-queue"
        }
    }
);

service / on listener_config {
    function init() returns error? {
    }

    resource function default orders(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};

        // set payload
        string payload0 = "orders ok";
        ctx.payload = payload0;

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

// TODO: placeholder jms listener for orders_mq_config
service "orders_mq_config" on orders_mq_config {
    function init() returns error? {
    }

    remote function onMessage(jms:Message message, jms:Caller caller) returns error? {
        Context ctx = {attributes: {jmsMessage: message}};
        log:printInfo("orders-queue message received");
    }
}
