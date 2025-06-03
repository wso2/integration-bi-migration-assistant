function pick(map<xml> context) returns xml | error {
    return scopeFn(context);
}