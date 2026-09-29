import ballerina/data.xmldata;

function Check(Context cx) returns error? {
}

function HandlerScopeActivityRunner(Context cx) returns error? {
    check Check(cx);
    if test_conditional_MainProcess_predicate_0(xml `<root></root>`, cx) {
        check Recover(cx);
    }
}

function HandlerScopeFaultHandler(error err, Context cx) returns () {
    panic err;
}

function HandlerScopeScopeFn(Context cx) returns () {
    error? result = HandlerScopeActivityRunner(cx);
    if result is error {
        HandlerScopeFaultHandler(result, cx);
    }
}

function MainScopeActivityRunner(Context cx) returns error? {
    check RaiseFault(cx);
}

function MainScopeFaultHandler(error err, Context cx) returns () {
    checkpanic catchAll(cx);
    return;
}

function MainScopeScopeFn(Context cx) returns () {
    error? result = MainScopeActivityRunner(cx);
    if result is error {
        MainScopeFaultHandler(result, cx);
    }
}

function RaiseFault(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    error var1 = error("tns:TestFault", faultName = "tns:TestFault", payload = var0);
    panic var1;
}

function Recover(Context cx) returns error? {
}

function catchAll(Context cx) returns error? {
    HandlerScopeScopeFn(cx);
}

function start_test_conditional_MainProcess(Context params) returns () {
    MainScopeScopeFn(params);
}

function test_conditional_MainProcess_predicate_0(xml input, Context cx) returns boolean {
    return checkpanic xmldata:transform(input, `${getFromContext(cx, "retry")} = 'true'`, boolean);
}

function getFromContext(Context context, string varName) returns xml {
    xml? value = context.variables[varName];
    if value == () {
        return xml `<root/>`;
    }
    return value;
}
