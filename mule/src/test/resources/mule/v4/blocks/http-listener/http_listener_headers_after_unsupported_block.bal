import ballerina/http;

public type Vars record {|
    anydata inboundOrderType?;
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

public listener http:Listener config = new (8081);

service /mule4 on config {
    function init() returns error? {
    }

    resource function get .(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};
        ctx.vars.inboundOrderType = getOptionalHeader(<http:Request>ctx.attributes.request, "orderType");

        // TODO: UNSUPPORTED MULE BLOCK ENCOUNTERED. MANUAL CONVERSION REQUIRED.
        // ------------------------------------------------------------------------
        // <jms:consume config-ref="jms_config" destination="orders" xmlns:doc="http://www.mulesoft.org/schema/mule/documentation" doc:name="Consume" xmlns:jms="http://www.mulesoft.org/schema/mule/jms"/>
        // ------------------------------------------------------------------------

        // TODO: failed to convert #[attributes.headers.'orderType']

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

function getOptionalHeader(http:Request req, string name) returns string? {
    string|http:HeaderNotFoundError value = req.getHeader(name);
    return value is string ? value : ();
}
