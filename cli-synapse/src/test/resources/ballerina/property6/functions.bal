function bar(Context ctx) {
    ctx.shared = "from-bar";
}

function foo(Context ctx) {
    bar(ctx);
    ctx.shared = "from-foo";
}
