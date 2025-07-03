function Null(Context cx) returns error? {
    xml var0 = xml`<root></root>`;
    xml var1 = xml`<root>${var0}</root>`;
    addToContext(cx, "Null", var1);
}
