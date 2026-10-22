import ballerina/xslt;

import testOrg/sharedmodule;

function CallHelper(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"><xsl:template name="Template" match="/"><Request/></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = transform(var2);
    addToContext(cx, "Start", var3);
    sharedmodule:start_sharedmodule_subs_Helper(cx);
    xml var4 = cx.result;
    xml var5 = xml `<root>${var4}</root>`;
    addToContext(cx, "CallHelper", var5);
}

function MainScopeActivityRunner(Context cx) returns error? {
    check CallHelper(cx);
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

function start_appmodule_Main(Context params) returns () {
    MainScopeScopeFn(params);
}

xmlns "http://www.tibco.com/bpel/2007/extensions" as tibex;
xmlns "http://docs.oasis-open.org/wsbpel/2.0/process/executable" as bpws;
