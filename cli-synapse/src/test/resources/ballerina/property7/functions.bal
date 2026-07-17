function bar(Context ctx) {
    ctx.variables.shared = 99;
}

function foo(Context ctx) {
    bar(ctx);
    ctx.variables.shared = "from-foo";
}
