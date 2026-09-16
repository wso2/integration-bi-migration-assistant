function FallbackScopeActivityRunner(Context cx) returns error? {
}

function FallbackScopeFaultHandler(error err, Context cx) returns () {
    panic err;
}

function FallbackScopeScopeFn(Context cx) returns () {
    error? result = FallbackScopeActivityRunner(cx);
    if result is error {
        FallbackScopeFaultHandler(result, cx);
    }
}

function MainScopeActivityRunner(Context cx) returns error? {
    check throw(cx);
}

function MainScopeFaultHandler(error err, Context cx) returns () {
    if err.detail()["faultName"] == "tns:TestFault" {
        addToContext(cx, "TestFaultVar", checkpanic err.detail()["payload"].ensureType());
        checkpanic catch(cx);
        return;
    }
    checkpanic catchAll(cx);
    return;
}

function MainScopeScopeFn(Context cx) returns () {
    error? result = MainScopeActivityRunner(cx);
    if result is error {
        MainScopeFaultHandler(result, cx);
    }
}

function RecoveryScopeActivityRunner(Context cx) returns error? {
}

function RecoveryScopeFaultHandler(error err, Context cx) returns () {
    panic err;
}

function RecoveryScopeScopeFn(Context cx) returns () {
    error? result = RecoveryScopeActivityRunner(cx);
    if result is error {
        RecoveryScopeFaultHandler(result, cx);
    }
}

function catch(Context cx) returns error? {
    RecoveryScopeScopeFn(cx);
}

function catchAll(Context cx) returns error? {
    FallbackScopeScopeFn(cx);
}

function start_test_faulthandling_MainProcess(Context params) returns () {
    MainScopeScopeFn(params);
}

function throw(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    error var1 = error("tns:TestFault", faultName = "tns:TestFault", payload = var0);
    panic var1;
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}
