function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "InputVariable");
    xml var1 = check xmldata:transform(var0, `.`);
    xml var2 = xml`<root>${var1}</root>`;
    addToContext(cx, "OutputVariable", var2);
}
