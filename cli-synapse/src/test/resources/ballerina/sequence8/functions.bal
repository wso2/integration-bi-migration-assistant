import ballerina/http;

function foo(http:Response response) {
    response.setPayload({"id": "002"});
}

function wrapper(http:Response response) {
    foo(response);
}
