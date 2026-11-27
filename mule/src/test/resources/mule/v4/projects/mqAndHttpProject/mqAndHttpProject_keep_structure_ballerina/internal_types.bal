import ballerina/http;
import ballerinax/java.jms;

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
    jms:Message jmsMessage?;
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};
