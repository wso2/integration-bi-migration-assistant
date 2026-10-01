import ballerina/http;
import ballerina/xslt;

function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "SendNotification-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://www.tibco.com/namespaces/tnt/plugins/restinvoke+7c3a1e59-2d84-4b6f-a0e9-6f1b5c8d3a27+RestInvokeInput" version="2.0"><xsl:template name="SendNotification-input" match="/"><tns1:RestActivityInput><MessageBody><asciiContent><xsl:value-of select="'{'event': 'orders-archived'}'"/></asciiContent></MessageBody></tns1:RestActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    // WARNING: REST Invoke path and query parameters are not mapped.
    map<string> var3 = {"Accept": "application/json"};
    foreach xml header in var2/**/<HttpHeaders>/* {
        if header is xml:Element && header.getName() != "DynamicHeaders" && header.data() != "" {
            var3[header.getName()] = header.data();
        }
    }
    foreach xml header in var2/**/<DynamicHeaders>/<Header> {
        var3[(header/<Name>).data()] = (header/<Value>).data();
    }
    string var4 = (var2/**/<ResourcePath>).data();
    string var5 = (var2/**/<MessageBody>/<asciiContent>).data();
    http:Response var6 = check Notify_HttpClientResource->execute("POST", var4 == "" ? "/api/notifications" : var4, var5, var3, "application/json");
    string|error var7 = var6.getTextPayload();
    string var8 = var7 is string ? var7 : "";
    if var6.statusCode >= 400 {
        string fault = var6.statusCode >= 500 ? "HttpServerException" : "HttpClientException";
        return error(string `${fault}: HTTP ${var6.statusCode} ${var6.reasonPhrase}: ${var8}`);
    }
    xml var9 = xml `<root><statusCode>${var6.statusCode}</statusCode><reasonPhrase>${var6.reasonPhrase}</reasonPhrase><MessageBody><asciiContent>${var8}</asciiContent></MessageBody></root>`;
    addToContext(cx, "SendNotification", var9);
}

function activityExtension_1(Context cx) returns error? {
    xml var0 = getFromContext(cx, "CheckStatus-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://www.tibco.com/namespaces/tnt/plugins/restinvoke+2f8d6b13-9e47-4c5a-b1d0-8a3e7f2c6d94+RestInvokeInput" version="2.0"><xsl:template name="CheckStatus-input" match="/"><tns1:RestActivityInput><HttpHeaders><DynamicHeaders><Header><Name><xsl:value-of select="'x-request-source'"/></Name><Value><xsl:value-of select="'orders-archive'"/></Value></Header></DynamicHeaders></HttpHeaders></tns1:RestActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    // WARNING: REST Invoke path and query parameters are not mapped.
    map<string> var3 = {"Accept": "application/json"};
    foreach xml header in var2/**/<HttpHeaders>/* {
        if header is xml:Element && header.getName() != "DynamicHeaders" && header.data() != "" {
            var3[header.getName()] = header.data();
        }
    }
    foreach xml header in var2/**/<DynamicHeaders>/<Header> {
        var3[(header/<Name>).data()] = (header/<Value>).data();
    }
    string var4 = (var2/**/<ResourcePath>).data();
    http:Response var5 = check Status_HttpClientResource->execute("GET", var4 == "" ? "/health" : var4, (), var3, ());
    string|error var6 = var5.getTextPayload();
    string var7 = var6 is string ? var6 : "";
    if var5.statusCode >= 400 {
        string fault = var5.statusCode >= 500 ? "HttpServerException" : "HttpClientException";
        return error(string `${fault}: HTTP ${var5.statusCode} ${var5.reasonPhrase}: ${var7}`);
    }
    xml var8 = xml `<root><statusCode>${var5.statusCode}</statusCode><reasonPhrase>${var5.reasonPhrase}</reasonPhrase><MessageBody><asciiContent>${var7}</asciiContent></MessageBody></root>`;
    addToContext(cx, "CheckStatus", var8);
}

function activityExtension_2(Context cx) returns error? {
    xml var0 = getFromContext(cx, "EndSession-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://www.tibco.com/namespaces/tnt/plugins/restinvoke+4b9e2c71-5d3a-4f86-a0c7-1e8d6f2b9a35+RestInvokeInput" version="2.0"><xsl:template name="EndSession-input" match="/"><tns1:RestActivityInput><MessageBody><asciiContent><xsl:value-of select="'{'reason': 'archive-complete'}'"/></asciiContent></MessageBody></tns1:RestActivityInput></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    // WARNING: REST Invoke path and query parameters are not mapped.
    map<string> var3 = {"Accept": "application/json"};
    foreach xml header in var2/**/<HttpHeaders>/* {
        if header is xml:Element && header.getName() != "DynamicHeaders" && header.data() != "" {
            var3[header.getName()] = header.data();
        }
    }
    foreach xml header in var2/**/<DynamicHeaders>/<Header> {
        var3[(header/<Name>).data()] = (header/<Value>).data();
    }
    string var4 = (var2/**/<ResourcePath>).data();
    string var5 = (var2/**/<MessageBody>/<asciiContent>).data();
    http:Response var6 = check Secure_HttpClientResource->execute("DELETE", var4 == "" ? "/api/sessions/current" : var4, var5, var3, "application/json");
    string|error var7 = var6.getTextPayload();
    string var8 = var7 is string ? var7 : "";
    if var6.statusCode >= 400 {
        string fault = var6.statusCode >= 500 ? "HttpServerException" : "HttpClientException";
        return error(string `${fault}: HTTP ${var6.statusCode} ${var6.reasonPhrase}: ${var8}`);
    }
    xml var9 = xml `<root><statusCode>${var6.statusCode}</statusCode><reasonPhrase>${var6.reasonPhrase}</reasonPhrase><MessageBody><asciiContent>${var8}</asciiContent></MessageBody></root>`;
    addToContext(cx, "EndSession", var9);
}

function receiveEvent(Context cx) returns error? {
}

function scopeActivityRunner(Context cx) returns error? {
    check receiveEvent(cx);
    check activityExtension(cx);
    check activityExtension_1(cx);
    check activityExtension_2(cx);
}

function scopeFaultHandler(error err, Context cx) returns () {
    panic err;
}

function scopeScopeFn(Context cx) returns () {
    error? result = scopeActivityRunner(cx);
    if result is error {
        scopeFaultHandler(result, cx);
    }
}

function start_test_rest_NotifyProcess(Context params) returns () {
    scopeScopeFn(params);
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
