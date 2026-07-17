function baz(Context ctx) {
    ctx.variables.str = "Hello world";
}

function bar(Context ctx) {
    baz(ctx);
}

function foo(Context ctx) {
    bar(ctx);
}
