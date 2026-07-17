function bar(Context ctx) {
    ctx.variables.shared = "from-bar";
}

function foo(Context ctx) {
    bar(ctx);
    ctx.variables.shared = "from-foo";
}
