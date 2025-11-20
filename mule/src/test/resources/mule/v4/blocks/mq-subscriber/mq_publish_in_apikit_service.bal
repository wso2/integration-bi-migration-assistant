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
public listener http:Listener http\-listener\-config = new (8081);

service / on http\-listener\-config {
    jms:MessageProducer producer0;

    function init() returns error? {
        jms:Connection connection0 = check new (mq_configConfig);
        jms:Session session0 = check connection0->createSession();
        self.producer0 = check session0.createProducer({'type: jms:QUEUE, name: "orders-queue"});
    }

    resource function default api(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        http:Client apiKitClient = check new ("http://localhost:8081");
        string apiKitRedirectPath = "/apikit0/" + request.rawPath.substring("/".length() + "api".length());
        match request.method {
            "GET" => {
                ctx.payload = check apiKitClient->get(apiKitRedirectPath);
            }
            "POST" => {
                ctx.payload = check apiKitClient->post(apiKitRedirectPath, check request.getJsonPayload());
            }
            "PUT" => {
                ctx.payload = check apiKitClient->put(apiKitRedirectPath, check request.getJsonPayload());
            }
            "DELETE" => {
                ctx.payload = check apiKitClient->delete(apiKitRedirectPath, check request.getJsonPayload());
            }
            _ => {
                panic error("Method not allowed");
            }
        }

        // TODO: try to directly call the endpoints generated for the api kit

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }

    resource function post apikit0/orders(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        log:printInfo("Processing new order");
        jms:MapMessage jmsMessage0 = {
            content: {
                "apiEndpoint": "POST /orders",
                "processedAt": now()
            }
        };
        check self.producer0->send(jmsMessage0);

        // set payload
        string payload0 = "{\"status\": \"Order received and queued\"}";
        ctx.payload = payload0;

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}
