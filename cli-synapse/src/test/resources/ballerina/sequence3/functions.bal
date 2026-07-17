function bar(Context ctx) {
    ctx.variables.str = "Hello world";
}

function foo(Context ctx) {
    ctx.variables.i = 23;
    bar(ctx);
}
