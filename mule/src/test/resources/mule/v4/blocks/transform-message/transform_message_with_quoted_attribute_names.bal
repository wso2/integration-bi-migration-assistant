import ballerina/http;

public type Vars record {|
    json _dwOutput_?;
    string orderText?;
    string orderTextOrDefault?;
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

public listener http:Listener listener_config = new (8081);

service /mule4 on listener_config {
    function init() returns error? {
    }

    resource function get orders/[string order\-id](http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new, uriParams: {order\-id}}};
        json _dwOutput_ = check transformMessage(ctx);
        ctx.vars._dwOutput_ = _dwOutput_;
        ctx.payload = _dwOutput_;
        string orderText = check formatOrderId(ctx);
        ctx.vars.orderText = orderText;
        string orderTextOrDefault = formatOrderId1(ctx);
        ctx.vars.orderTextOrDefault = orderTextOrDefault;

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

public function transformMessage(Context ctx) returns json|error => {
    "apiKey": check ((<http:Request>ctx.attributes.request).hasHeader("x-api-key") ? (<http:Request>ctx.attributes.request).getHeader("x-api-key") : ()),
    "channel": check ((<http:Request>ctx.attributes.request).hasHeader("channel") ? (<http:Request>ctx.attributes.request).getHeader("channel") : ()),
    "channelOrDefault": (check ((<http:Request>ctx.attributes.request).hasHeader("channel") ? (<http:Request>ctx.attributes.request).getHeader("channel") : ())) ?: "web",
    "orderId": (ctx.attributes.uriParams.hasKey("order-id") ? ctx.attributes.uriParams.get("order-id") : ()),
    "orderIdOrDefault": ((ctx.attributes.uriParams.hasKey("order-id") ? ctx.attributes.uriParams.get("order-id") : ())) ?: "none",
    "orderLabel": "Order " + (ctx.attributes.uriParams.hasKey("order-id") ? ctx.attributes.uriParams.get("order-id") : ()),
    "pageSize": (<http:Request>ctx.attributes.request).getQueryParamValue("page-size"),
    "sortBy": (<http:Request>ctx.attributes.request).getQueryParamValue("sort-by") ?: "date"
};

public function formatOrderId(Context ctx) returns string|error => check (ctx.attributes.uriParams.hasKey("order-id") ? ctx.attributes.uriParams.get("order-id") : error("no such uri param"));

public function formatOrderId1(Context ctx) returns string => ((ctx.attributes.uriParams.hasKey("order-id") ? ctx.attributes.uriParams.get("order-id") : ())) ?: "none";
