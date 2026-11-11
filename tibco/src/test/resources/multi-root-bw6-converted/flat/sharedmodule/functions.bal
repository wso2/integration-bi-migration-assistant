function HelperScopeActivityRunner(Context cx) returns error? {
}

function HelperScopeFaultHandler(error err, Context cx) returns error? {
    return err;
}

public function HelperScopeScopeFn(Context cx) returns error? {
    error? result = HelperScopeActivityRunner(cx);
    if result is error {
        check HelperScopeFaultHandler(result, cx);
    }
}

public function start_sharedmodule_subs_Helper(Context params) returns error? {
    return HelperScopeScopeFn(params);
}
