import ballerina/file;
import ballerina/regex;
import ballerina/xslt;

function HTTP_Receiver(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = xml `<root>${var0}</root>`;
    addToContext(cx, "HTTP-Receiver", var1);
}

function List_Files(Context cx) returns error? {
    xml var0 = xml `<root></root>`;
    xml var1 = check xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:pd="http://xmlns.tibco.com/bw/process/2003" xmlns:ns="http://www.tibco.com/pe/EngineTypes" xmlns:xsd="http://www.w3.org/2001/XMLSchema" version="2.0">
     <xsl:template name="Transform0" match="/">
        <ns1:ListFilesActivityConfig xmlns:ns1="http://www.tibco.com/namespaces/tnt/plugins/file">
                    
    <fileName>test/path/*.txt</fileName>
                
</ns1:ListFilesActivityConfig>

    </xsl:template>
</xsl:stylesheet>`, cx.variables);
    // WARNING: Only fileName and fullName are supported in ListFilesActivity output.
    string var2 = (var1/**/<fileName>/*).toString().trim();
    FileData[] var3 = check filesInPath(var2, false);
    xml var4 = xml ``;
    foreach FileData file in var3 {
        var4 += xml `<fileInfo>
                    <fileName>${file.fileName}</fileName>
                    <fullName>${file.fullName}</fullName>
               </fileInfo>`;
    }
    xml var5 = xml `<root>
    <ListFilesActivityOutput xmlns="http://www.tibco.com/namespaces/tnt/plugins/file">
        <files>${var4}</files>
    </ListFilesActivityOutput>
</root>`;
    addToContext(cx, "List-Files", var5);
}

function scope0ActivityRunner(Context cx) returns error? {
    check HTTP_Receiver(cx);
    check List_Files(cx);
}

function scope0FaultHandler(error err, Context cx) returns () {
    panic err;
}

function scope0ScopeFn(Context cx) returns () {
    error? result = scope0ActivityRunner(cx);
    if result is error {
        scope0FaultHandler(result, cx);
    }
}

function start_Processes_Main_process(Context cx) returns () {
    return scope0ScopeFn(cx);
}

function getFileName(string absPath) returns string {
    int? index = absPath.lastIndexOf("/");
    if index == () {
        return absPath;
    }
    return absPath.substring(index + 1, absPath.length());
}

function filesInPath(string path, boolean allowDir) returns FileData[]|error {
string basePath = path;
string? pattern = ();
if path.includes("*") {
int? index = path.lastIndexOf("/");
if index == () {
basePath = ".";
pattern = path;
} else {
basePath = path.substring(0, index);
pattern = path.substring(index + 1, path.length());
}
}
if pattern != () {
pattern = regex:replaceAll(pattern, "\\*", ".*");
}
file:MetaData[] entries = check file:readDir(basePath);
FileData[] result = [];
foreach file:MetaData entry in entries {
if entry.dir && !allowDir {
continue;
}
string fileName = getFileName(entry.absPath);
if pattern == () {
result.push({fileName: fileName, fullName: entry.absPath});
} else if regex:matches(fileName, pattern) {
result.push({fileName: fileName, fullName: entry.absPath});
}
}
return result;
}

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}

function initContext(map<xml> initVariables = {},
        map<SharedVariableContext> jobSharedVariables = {})
            returns Context {
    map<SharedVariableContext> sharedVariables = {};

    foreach var key in jobSharedVariables.keys() {
        sharedVariables[key] = jobSharedVariables.get(key);
    }
    return {variables: initVariables, result: xml `<root/>`, sharedVariables};
}
