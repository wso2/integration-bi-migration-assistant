function receiveEvent(Context context) returns xml | error {
    addToContext(context, "Start", getFromContext(context, "$input"));
    return getFromContext(context, "$input");
}
