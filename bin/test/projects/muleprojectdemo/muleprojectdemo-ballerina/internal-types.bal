import ballerina/http;

public type InboundProperties record {|
    http:Response response;
|};

public type Context record {|
    anydata payload;
    InboundProperties inboundProperties;
|};
