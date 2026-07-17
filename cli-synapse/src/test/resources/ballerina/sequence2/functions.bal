function foo(Context ctx) {
    ctx.variables.i = 23;
}

function sequence(Context ctx) {
    ctx.variables.str = "Hello world";
    foo(ctx);
}
