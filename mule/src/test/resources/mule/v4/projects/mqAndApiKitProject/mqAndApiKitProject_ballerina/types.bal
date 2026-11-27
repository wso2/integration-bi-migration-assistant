import ballerina/http;
import ballerinax/java.jms;

public type APIKIT__NOT_FOUND distinct error;

public type Vars record {|
    anydata httpStatus?;
|};

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
    jms:Message jmsMessage?;
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
    Attributes attributes;
|};
