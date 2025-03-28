type InboundProperties record {|
    http:Response response;
|};

type Context record {|
    anydata payload;
    InboundProperties inboundProperties;
|};
