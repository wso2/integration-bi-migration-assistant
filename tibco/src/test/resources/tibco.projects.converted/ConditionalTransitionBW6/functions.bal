import ballerina/data.xmldata;

function HandlerScopeActivityRunner(Context cx) returns error? {
    if check test_conditional_MainProcess_predicate_0(xml `<root></root>`, cx) {
        check Recover(cx);
    }
}

function HandlerScopeFaultHandler(error err, Context cx) returns error? {
    return err;
}

function HandlerScopeScopeFn(Context cx) returns error? {
    error? result = HandlerScopeActivityRunner(cx);
    if result is error {
        check HandlerScopeFaultHandler(result, cx);
    }
}

function MainScopeActivityRunner(Context cx) returns error? {
    check RaiseFault(cx);
}

function MainScopeFaultHandler(error err, Context cx) returns error? {
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

function Recover(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    error var1 = error("tns:RetryFault", faultName = "tns:RetryFault", payload = var0);
    return var1;
}

function catchAll(Context cx) returns error? {
    check HandlerScopeScopeFn(cx);
}

function start_test_conditional_MainProcess(Context params) returns error? {
    return MainScopeScopeFn(params);
}

function test_conditional_MainProcess_predicate_0(xml input, Context cx) returns boolean|error {
    return check xmldata:transform(input, `${getFromContext(cx, "retry")} = 'true'`, boolean);
}

function getFromContext(Context context, string varName) returns xml {
    xml? value = context.variables[varName];
    if value == () {
        return xml `<root/>`;
    }
    return value;
}
