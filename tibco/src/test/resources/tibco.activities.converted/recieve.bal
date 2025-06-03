function receiveEvent(map<xml> context) returns xml | error {
    addToContext(context, "Start", context.get("$input"));
    return context.get("$input");
}
