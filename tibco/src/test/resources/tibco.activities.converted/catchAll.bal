function catchAll(Context context) returns xml | error {
    return scopeFn(context);
}
