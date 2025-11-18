import ballerina/http;
import ballerina/log;

public type Attributes record {|
    http:Request request?;
    http:Response response?;
    map<string> uriParams = {};
|};

public type Context record {|
    anydata payload = ();
    Attributes attributes;
|};

public listener http:Listener HTTP_Listener_config = new (9090);

service / on HTTP_Listener_config {
    function init() returns error? {
    }

    resource function default scatter(http:Request request) returns http:Response|error {
        Context ctx = {attributes: {request, response: new}};

        // scatter-gather parallel execution
        fork {
            worker R0 returns anydata|error {
                // Route 0

                // set payload
                string payload0 = "Route 1 completed";
                ctx.payload = payload0;
                return ctx.payload;
            }
            worker R1 returns anydata|error {
                // Route 1

                // set payload
                string payload1 = "Route 2 completed";
                ctx.payload = payload1;
                return ctx.payload;
            }
            worker R2 returns anydata|error {
                // Route 2

                // set payload
                string payload2 = "Route 3 completed";
                ctx.payload = payload2;
                return ctx.payload;
            }
        }

        // wait for all workers to complete
        map<anydata|error> workerResults0 = wait {R0, R1, R2};
        map<anydata> scatterGatherResults0 = workerResults0.entries().'map(e => wrapRouteErrorIfExists(e[0], e[1])).'map(m => check m);
        ctx.payload = scatterGatherResults0;

        log:printInfo(ctx.payload.toString());

        (<http:Response>ctx.attributes.response).setPayload(ctx.payload);
        return <http:Response>ctx.attributes.response;
    }
}

public function wrapRouteErrorIfExists(string key, anydata|error value) returns anydata|error {
    if value is error {
        return error(string `Error in Route ${key}: ${value.message()}`, value);
    }
    return value;
}
