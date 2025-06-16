function receiveEvent(Context cx) returns error? {
    addToContext(cx, "Start", getFromContext(cx, "$input"));
}
