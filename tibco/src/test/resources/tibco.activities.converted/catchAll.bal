function catchAll(map<xml> context) returns xml | error {
    return scopeFn(context);
}