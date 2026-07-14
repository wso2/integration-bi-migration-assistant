function bar(Context ctx) {
    ctx.barProp1 = "bar-one";
    ctx.barProp2 = 10;
}

function foo(Context ctx) {
    ctx.before = "before";
    bar(ctx);
    ctx.after = "after";
}
