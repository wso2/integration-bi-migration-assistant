function foo(Context ctx) {
    ctx.i = 23;
}

function sequence(Context ctx) {
    ctx.str = "Hello world";
    foo(ctx);
}
