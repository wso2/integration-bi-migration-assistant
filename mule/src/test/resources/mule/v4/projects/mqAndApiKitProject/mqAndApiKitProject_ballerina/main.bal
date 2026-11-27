import ballerina/http;
import ballerina/log;
import ballerinax/java.jms;

jms:ConnectionConfiguration orders_mq_configConfig = {initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory", providerUrl: JMS_PROVIDER_URL};
jms:ConnectionConfiguration partner_mq_configConfig = {initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory", providerUrl: JMS_PROVIDER_URL};
public listener jms:Listener orders_mq_config = new jms:Listener(
    connectionConfig = orders_mq_configConfig,
    consumerOptions = {
        destination: {
            'type: jms:QUEUE,
            name: "apiKitMain-queue"
        }
    }
);
public listener jms:Listener partner_mq_config = new jms:Listener(
    connectionConfig = partner_mq_configConfig,
    consumerOptions = {
        destination: {
            'type: jms:QUEUE,
            name: "partnerApiKitMain-queue"
        }
    }
);
public listener http:Listener http\-listener\-config = new (check int:fromString(http_port));
public listener http:Listener partner\-http\-listener\-config = new (check int:fromString(partner_http_port));

service http:InterceptableService / on http\-listener\-config {
    function init() returns error? {
    }

    public function createInterceptors() returns [MuleResponseErrorInterceptor0] {
        return [new MuleResponseErrorInterceptor0()];
    }

    resource function default api/[string... path](http:Request request) returns http:Response|error {
        return error APIKIT__NOT_FOUND("APIKIT:NOT_FOUND");
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

service http:InterceptableService / on partner\-http\-listener\-config {
    function init() returns error? {
    }

    public function createInterceptors() returns [MuleResponseErrorInterceptor1] {
        return [new MuleResponseErrorInterceptor1()];
    }

    resource function default partner/[string... path](http:Request request) returns http:Response|error {
        return error APIKIT__NOT_FOUND("APIKIT:NOT_FOUND");
    }

    resource function get api/orders(string id, http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        log:printInfo("get orders");

        http:Response response = <http:Response>ctx.attributes.response;
        response.setPayload(ctx.payload);

        // http response status code
        response.statusCode = check int:fromString((ctx.vars?.httpStatus ?: 200).toString());
        return response;
    }

    resource function get partner/orders(string id, http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        log:printInfo("get partner orders");

        http:Response response = <http:Response>ctx.attributes.response;
        response.setPayload(ctx.payload);

        // http response status code
        response.statusCode = check int:fromString((ctx.vars?.httpStatus ?: 200).toString());
        return response;
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

service class MuleResponseErrorInterceptor0 {
    *http:ResponseErrorInterceptor;

    remote function interceptResponseError(http:RequestContext requestContext, http:Response interceptedResponse, error err) returns http:Response|error {
        Context ctx = {attributes: {response: interceptedResponse}};
        // on-error-continue

        // set payload
        string payload0 = "{\"message\": \"Resource not found\"}";
        ctx.payload = payload0;
        ctx.vars.httpStatus = "404";

        // set payload
        anydata payload1 = ctx.payload;
        ctx.payload = payload1;
        if ctx.payload !is () {
            (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        }

        // http response status code
        interceptedResponse.statusCode = check int:fromString((ctx.vars?.httpStatus ?: 500).toString());
        return <http:Response>ctx.attributes.response;
    }
}

service class MuleResponseErrorInterceptor1 {
    *http:ResponseErrorInterceptor;

    remote function interceptResponseError(http:RequestContext requestContext, http:Response interceptedResponse, error err) returns http:Response|error {
        Context ctx = {attributes: {response: interceptedResponse}};
        // on-error-continue

        // set payload
        string payload2 = "{\"message\": \"Resource not found\"}";
        ctx.payload = payload2;
        ctx.vars.httpStatus = "404";

        // set payload
        anydata payload3 = ctx.payload;
        ctx.payload = payload3;
        if ctx.payload !is () {
            (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        }

        // http response status code
        interceptedResponse.statusCode = check int:fromString((ctx.vars?.httpStatus ?: 500).toString());
        return <http:Response>ctx.attributes.response;
    }
}
