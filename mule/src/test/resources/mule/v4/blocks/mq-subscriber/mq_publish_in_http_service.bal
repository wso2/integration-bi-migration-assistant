import ballerina/http;
import ballerina/log;
import ballerinax/java.jms;

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

configurable string JMS_PROVIDER_URL = ?;
jms:ConnectionConfiguration mq_configConfig = {initialContextFactory: "org.apache.activemq.jndi.ActiveMQInitialContextFactory", providerUrl: JMS_PROVIDER_URL};
public listener http:Listener http_config = new (8081);

service /api on http_config {
    jms:MessageProducer producer0;

    function init() returns error? {
        jms:Connection connection0 = check new (mq_configConfig);
        jms:Session session0 = check connection0->createSession();
        self.producer0 = check session0.createProducer({'type: jms:QUEUE, name: "orders-queue"});
    }

    resource function post send\-message(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        log:printInfo("Processing HTTP request");
        jms:MapMessage jmsMessage0 = {
            content: {
                "source": "http-service",
                "timestamp": now()
            }
        };
        check self.producer0->send(jmsMessage0);

        // set payload
        string payload0 = "{\"status\": \"Message published successfully\"}";
        ctx.payload = payload0;

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
