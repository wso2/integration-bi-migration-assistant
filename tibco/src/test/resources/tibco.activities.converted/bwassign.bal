function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "InputVariable");
    xml var1 = checkpanic xmldata:transform(xml`<root></root>`, `concat(${getFromContext(cx, "InputVariable")}, '.csv')`);
    xml var2 = xml`<root>${var1}</root>`;
    addToContext(cx, "OutputVariable", var2);
}
