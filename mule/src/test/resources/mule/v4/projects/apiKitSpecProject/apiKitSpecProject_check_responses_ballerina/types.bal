import ballerina/constraint;
import ballerina/http;

public type APIKIT__NOT_FOUND distinct error;

public type APIKIT__NOT_IMPLEMENTED distinct error;

public type APIKIT__BAD_REQUEST distinct error;

public type Vars record {|
    anydata outboundHeaders?;
    anydata httpStatus?;
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
|};

public type Status "NEW"|"SHIPPED";

public type Address record {
    string street;
    string town?;
};

public type Order record {
    @constraint:String {pattern: re `^[0-9]+$`, maxLength: 10}
    string orderId;
    Status status;
    @constraint:Int {minValue: 1, maxValue: 99}
    int quantity;
    string note?;
    @constraint:Array {minLength: 1}
    Address[] addresses;
};

public type PriorityOrder record {
    *Order;
    @constraint:String {pattern: re `^[0-9]+$`, maxLength: 10}
    string orderId; // repeated from Order so that Ballerina checks its constraint
    @constraint:Int {minValue: 1, maxValue: 99}
    int quantity; // repeated from Order so that Ballerina checks its constraint
    @constraint:Array {minLength: 1}
    Address[] addresses; // repeated from Order so that Ballerina checks its constraint
    boolean priority;
};

@constraint:String {pattern: re `^[A-Za-z ]+$`}
public type PersonName string;

public type Note record {|
    @constraint:String {maxLength: 20}
    string text;
    PersonName? author?; // TODO: the spec limits this field, but Ballerina cannot check a field that may be nil: {pattern: re `^[A-Za-z ]+$`}
|};

@constraint:String {pattern: re `^[A-Z]{2}$`}
public type MarketId string;

public type PriorityOrderOk record {|
    *http:Ok;
    PriorityOrder body;
|};

public type OrderCreated record {|
    *http:Created;
    Order body;
|};
