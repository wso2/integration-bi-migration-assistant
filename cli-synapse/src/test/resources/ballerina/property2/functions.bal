function bar(Context ctx) {
    ctx.variables.barProp1 = "bar-one";
    ctx.variables.barProp2 = 10;
}

function foo(Context ctx) {
    ctx.variables.before = "before";
    bar(ctx);
    ctx.variables.after = "after";
}
