function HelperScopeActivityRunner(Context cx) returns error? {
}

function HelperScopeFaultHandler(error err, Context cx) returns () {
    panic err;
}

public function HelperScopeScopeFn(Context cx) returns () {
    error? result = HelperScopeActivityRunner(cx);
    if result is error {
        HelperScopeFaultHandler(result, cx);
    }
}

public function start_sharedmodule_subs_Helper(Context params) returns () {
    HelperScopeScopeFn(params);
}

xmlns "http://www.tibco.com/bpel/2007/extensions" as tibex;
xmlns "http://docs.oasis-open.org/wsbpel/2.0/process/executable" as bpws;
