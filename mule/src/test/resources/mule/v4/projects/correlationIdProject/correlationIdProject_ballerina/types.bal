import ballerina/http;
import ballerina/uuid;

public type Vars record {|
    json _dwOutput_?;
|};

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Vars vars = {};
    Attributes attributes;
    string correlationId = uuid:createType4AsString();
|};
