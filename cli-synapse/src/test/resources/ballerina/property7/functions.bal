function bar(Context ctx) {
    ctx.shared = 99;
}

function foo(Context ctx) {
    bar(ctx);
    ctx.shared = "from-foo";
}
