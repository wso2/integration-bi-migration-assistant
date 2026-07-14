function bar(Context ctx) {
    ctx.str = "Hello world";
}

function foo(Context ctx) {
    ctx.i = 23;
    bar(ctx);
}
