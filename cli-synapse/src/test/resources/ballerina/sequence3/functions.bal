import ballerina/http;

function bar() {
    string str = "Hello world";
}

function foo(http:Response response) {
    int i = 23;
    bar();
}
