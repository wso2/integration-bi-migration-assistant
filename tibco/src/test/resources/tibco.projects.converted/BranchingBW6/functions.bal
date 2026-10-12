import ballerina/data.xmldata;
import ballerina/log;
import ballerina/xslt;

function AltLog(Context cx) returns error? {
    xml var0 = getFromContext(cx, "AltLog-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0">
    <xsl:template name="AltLog-input" match="/">
        <tns:ActivityInput>
            <message>AltLog</message>
        </tns:ActivityInput>
    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
}

function Check(Context cx) returns error? {
    xml var0 = getFromContext(cx, "Check-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0">
    <xsl:template name="Check-input" match="/">
        <tns:ActivityInput>
            <message>Check</message>
        </tns:ActivityInput>
    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
}

function End(Context cx) returns error? {
    xml var0 = getFromContext(cx, "End-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0">
    <xsl:template name="End-input" match="/">
        <tns:ActivityInput>
            <message>End</message>
        </tns:ActivityInput>
    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
}

function FailLog(Context cx) returns error? {
    xml var0 = getFromContext(cx, "FailLog-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0">
    <xsl:template name="FailLog-input" match="/">
        <tns:ActivityInput>
            <message>FailLog</message>
        </tns:ActivityInput>
    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
}

function FailThrow(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    error var1 = error("tns:CheckFault", faultName = "tns:CheckFault", payload = var0);
    panic var1;
}

function Join(Context cx) returns error? {
}

function MainScopeActivityRunner(Context cx) returns error? {
    check Check(cx);
    boolean CheckToOkTaken = test_branching_MainProcess_predicate_0(xml `<root></root>`, cx);
    boolean CheckToAltTaken = test_branching_MainProcess_predicate_1(xml `<root></root>`, cx);
    boolean CheckToFailTaken = !(CheckToOkTaken || CheckToAltTaken);
    if CheckToFailTaken {
        check FailLog(cx);
        check FailThrow(cx);
    }
    if CheckToAltTaken {
        check AltLog(cx);
    }
    if CheckToOkTaken {
        check OkLog(cx);
        check OkDone(cx);
    }
    boolean JoinReached = CheckToOkTaken || CheckToAltTaken;
    if JoinReached {
        check Join(cx);
    }
    error? RiskyResult = ();
    if JoinReached {
        RiskyResult = trap Risky(cx);
    }
    if RiskyResult is error {
        addToContext(cx, "_error", xml `<ns:ActivityErrorData xmlns:ns="http://www.tibco.com/pe/EngineTypes"><ProcessStack></ProcessStack><StackTrace>${RiskyResult.stackTrace().toString()}</StackTrace><Msg>${RiskyResult.message()}</Msg><MsgCode></MsgCode></ns:ActivityErrorData>`);
        addToContext(cx, "RiskyFault", xml `<ns:ActivityErrorData xmlns:ns="http://www.tibco.com/pe/EngineTypes"><ProcessStack></ProcessStack><StackTrace>${RiskyResult.stackTrace().toString()}</StackTrace><Msg>${RiskyResult.message()}</Msg><MsgCode></MsgCode></ns:ActivityErrorData>`);
        addToContext(cx, "FaultName", xml `<root>${RiskyResult.message()}</root>`);
    }
    boolean RiskyToRecoverTaken = RiskyResult is error;
    boolean RiskyToEndTaken = JoinReached && (RiskyResult !is error);
    if RiskyToEndTaken {
        check End(cx);
    }
    if RiskyToRecoverTaken {
        check Recover(cx);
    }
}

function MainScopeFaultHandler(error err, Context cx) returns () {
    panic err;
}

function MainScopeScopeFn(Context cx) returns () {
    error? result = MainScopeActivityRunner(cx);
    if result is error {
        MainScopeFaultHandler(result, cx);
    }
}

function OkDone(Context cx) returns error? {
    xml var0 = getFromContext(cx, "OkDone-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0">
    <xsl:template name="OkDone-input" match="/">
        <tns:ActivityInput>
            <message>OkDone</message>
        </tns:ActivityInput>
    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
}

function OkLog(Context cx) returns error? {
    xml var0 = getFromContext(cx, "OkLog-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0">
    <xsl:template name="OkLog-input" match="/">
        <tns:ActivityInput>
            <message>OkLog</message>
        </tns:ActivityInput>
    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
}

function Recover(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    error var1 = error("tns:RiskyFault", faultName = "tns:RiskyFault", payload = var0);
    panic var1;
}

function Risky(Context cx) returns error? {
    xml var0 = getFromContext(cx, "Risky-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="http://www.tibco.com/pe/WriteToLogActivitySchema" version="2.0">
    <xsl:template name="Risky-input" match="/">
        <tns:ActivityInput>
            <message>Risky</message>
        </tns:ActivityInput>
    </xsl:template>
</xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = var2/**/<message>/*;
    log:printInfo(var3.toString());
}

function start_test_branching_MainProcess(Context params) returns () {
    MainScopeScopeFn(params);
}

function test_branching_MainProcess_predicate_0(xml input, Context cx) returns boolean {
    return checkpanic xmldata:transform(input, `${getFromContext(cx, "mode")} = 'ok'`, boolean);
}

function test_branching_MainProcess_predicate_1(xml input, Context cx) returns boolean {
    return checkpanic xmldata:transform(input, `${getFromContext(cx, "mode")} = 'alt'`, boolean);
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}

function getFromContext(Context context, string varName) returns xml {
    xml? value = context.variables[varName];
    if value == () {
        return xml `<root/>`;
    }
    return value;
}
