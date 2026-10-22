function FallbackScopeActivityRunner(Context cx) returns error? {
}

function FallbackScopeFaultHandler(error err, Context cx) returns error? {
    return err;
}

function FallbackScopeScopeFn(Context cx) returns error? {
    error? result = FallbackScopeActivityRunner(cx);
    if result is error {
        check FallbackScopeFaultHandler(result, cx);
    }
}

function MainScopeActivityRunner(Context cx) returns error? {
    check RaiseFault(cx);
}

function MainScopeFaultHandler(error err, Context cx) returns error? {
    if err.detail()["faultName"] == "tns:TestFault" {
        xml|error payload = err.detail()["payload"].ensureType();
        addToContext(cx, "TestFaultVar", payload is xml ? payload : xml ``);
        check catch(cx);
        return;
    }
    check catchAll(cx);
    return;
}

function MainScopeScopeFn(Context cx) returns error? {
    error? result = MainScopeActivityRunner(cx);
    if result is error {
        check MainScopeFaultHandler(result, cx);
    }
}

function RaiseFault(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    error var1 = error("tns:TestFault", faultName = "tns:TestFault", payload = var0);
    return var1;
}

function RecoveryScopeActivityRunner(Context cx) returns error? {
}

function RecoveryScopeFaultHandler(error err, Context cx) returns error? {
    return err;
}

function RecoveryScopeScopeFn(Context cx) returns error? {
    error? result = RecoveryScopeActivityRunner(cx);
    if result is error {
        check RecoveryScopeFaultHandler(result, cx);
    }
}

function catch(Context cx) returns error? {
    check RecoveryScopeScopeFn(cx);
}

function catchAll(Context cx) returns error? {
    check FallbackScopeScopeFn(cx);
}

function start_test_faulthandling_MainProcess(Context params) returns error? {
    return MainScopeScopeFn(params);
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}
