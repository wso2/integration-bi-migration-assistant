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

xmlns "http://www.tibco.com/bpel/2007/extensions" as tibex;
xmlns "http://docs.oasis-open.org/wsbpel/2.0/process/executable" as bpws;
