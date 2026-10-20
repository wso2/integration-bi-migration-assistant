import ballerina/xslt;

import testOrg/sharedmodule;

function CallHelper(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" version="2.0"><xsl:template name="Template" match="/"><Request/></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    xml var3 = transform(var2);
    addToContext(cx, "Start", var3);
    check sharedmodule:start_sharedmodule_subs_Helper(cx);
    xml var4 = cx.result;
    xml var5 = xml `<root>${var4}</root>`;
    addToContext(cx, "CallHelper", var5);
}

function End(Context cx) returns error? {
}

function MainScopeActivityRunner(Context cx) returns error? {
    check Start(cx);
    check CallHelper(cx);
    check End(cx);
}

function MainScopeFaultHandler(error err, Context cx) returns error? {
    return err;
}

function MainScopeScopeFn(Context cx) returns error? {
    error? result = MainScopeActivityRunner(cx);
    if result is error {
        check MainScopeFaultHandler(result, cx);
    }
}

function Start(Context cx) returns error? {
}

function start_appmodule_Main(Context params) returns error? {
    return MainScopeScopeFn(params);
}

function transform(xml value) returns xml {
    xml result = transformInner(value);
    string str = result.toString();
    return checkpanic xml:fromString(str);
}

function transformInner(xml value) returns xml {
    xml result;
    if (value is xml:Element) {
        result = transformElement(value);
    } else {
        result = value;
    }
    return result;
}

function transformElement(xml:Element element) returns xml {
    XMLElementParseResult parseResult = parseElement(element);
    string? namespace = parseResult.namespace;

    xml:Element transformedElement = element.clone();
    transformedElement.setName(parseResult.name);
    map<string> attributes = transformedElement.getAttributes();
    if namespace != () {
        attributes["xmlns"] = namespace;
    }

    // Get children and transform them recursively
    xml children = element/*.clone();
    xml transformedChildren = children.map(transform);

    // Create new element with transformed children
    transformedElement.setChildren(transformedChildren);
    return transformedElement;
}

function parseElement(xml:Element element) returns XMLElementParseResult {
    string name = element.getName();
    if (name.startsWith("{")) {
        int? index = name.indexOf("}");
        if (index == ()) {
            panic error("Invalid element name: " + name);
        }
        string namespace = name.substring(1, index);
        name = name.substring(index + 1);
        return {namespace: namespace, name: name};
    }
    return {namespace: (), name: name};
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}
