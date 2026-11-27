import ballerinax/java.jms;

public type Attributes record {|
    map<string> uriParams = {};
    jms:Message jmsMessage?;
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};
