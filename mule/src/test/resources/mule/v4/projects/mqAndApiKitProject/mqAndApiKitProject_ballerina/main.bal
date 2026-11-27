import ballerina/http;
import ballerina/log;
import ballerinax/java.jms;

jms:ConnectionConfiguration partner_mq_configConfig = {initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory", providerUrl: JMS_PROVIDER_URL};
jms:ConnectionConfiguration orders_mq_configConfig = {initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory", providerUrl: JMS_PROVIDER_URL};
public listener jms:Listener partner_mq_config = new jms:Listener(
    connectionConfig = partner_mq_configConfig,
    consumerOptions = {
        destination: {
            'type: jms:QUEUE,
            name: "partnerApiKitMain-queue"
        }
    }
);
public listener jms:Listener orders_mq_config = new jms:Listener(
    connectionConfig = orders_mq_configConfig,
    consumerOptions = {
        destination: {
            'type: jms:QUEUE,
            name: "apiKitMain-queue"
        }
    }
);
public listener http:Listener http\-listener\-config = new (8080);

service / on http\-listener\-config {
    function init() returns error? {
    }

    resource function default partner/[string... path](http:Request request) returns http:Response|error {
        return error APIKIT__NOT_FOUND("APIKIT:NOT_FOUND");
    }
}

// TODO: placeholder jms listener for partner_mq_config
service "partner_mq_config" on partner_mq_config {
    function init() returns error? {
    }

    remote function onMessage(jms:Message message, jms:Caller caller) returns error? {
        Context ctx = {attributes: {jmsMessage: message}};
        log:printInfo("partnerApiKitMain-queue message received");
    }
}

service / on http\-listener\-config {
    function init() returns error? {
    }

    resource function default api/[string... path](http:Request request) returns http:Response|error {
        return error APIKIT__NOT_FOUND("APIKIT:NOT_FOUND");
    }

    resource function get api/orders(string id, http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        log:printInfo("get orders");

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }

    resource function get partner/orders(string id, http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        log:printInfo("get partner orders");

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
        log:printInfo("apiKitMain-queue message received");
    }
}
