import ballerina/file;
import ballerina/ftp;
import ballerina/io;
import ballerina/regex;
import ballerina/xslt;

function activityExtension(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ArchiveOrderFile-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/renameFile" version="2.0"><xsl:template name="ArchiveOrderFile-input" match="/"><tns1:Input><OldRemoteFileName><xsl:value-of select="concat('/inbound/', 'orders.csv')"/></OldRemoteFileName><NewRemoteFileName><xsl:value-of select="concat('/archive/', 'orders.csv')"/></NewRemoteFileName></tns1:Input></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = (var2/**/<OldRemoteFileName>/*).toString();
    string var4 = (var2/**/<NewRemoteFileName>/*).toString();
    check Orders_SftpConnection->rename(var3, var4);
}

function activityExtension_1(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ArchiveReport-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/renameFile" version="2.0"><xsl:template name="ArchiveReport-input" match="/"><tns1:Input><OldRemoteFileName><xsl:value-of select="'/reports/daily.pdf'"/></OldRemoteFileName><NewRemoteFileName><xsl:value-of select="'/reports/archive/daily.pdf'"/></NewRemoteFileName></tns1:Input></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = (var2/**/<OldRemoteFileName>/*).toString();
    string var4 = (var2/**/<NewRemoteFileName>/*).toString();
    check Reports_SftpConnection->rename(var3, var4);
}

function activityExtension_2(Context cx) returns error? {
    xml var0 = getFromContext(cx, "CleanupMarkerFiles-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/removeRemoteFile" version="2.0"><xsl:template name="CleanupMarkerFiles-input" match="/"><tns1:Input><RemoteFileName><xsl:value-of select="'/inbound/*.done'"/></RemoteFileName></tns1:Input></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = (var2/**/<RemoteDirectory>/*).toString() + (var2/**/<RemoteFileName>/*).toString();
    check sftpDeleteFiles(Orders_SftpConnection, var3);
}

function activityExtension_3(Context cx) returns error? {
    xml var0 = getFromContext(cx, "ListInbound-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/dir" version="2.0"><xsl:template name="ListInbound-input" match="/"><tns1:Input><Directory><xsl:value-of select="'/inbound/'"/></Directory></tns1:Input></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = (var2/**/<Directory>/*).toString();
    ftp:FileInfo[] var4 = check Orders_SftpConnection->list(var3 == "" ? "." : var3);
    xml var5 = xml ``;
    foreach ftp:FileInfo entry in var4 {
        var5 += xml `<DirectoryItems>${entry.name}</DirectoryItems>`;
    }
    xml var6 = xml `<root><ItemCount>${var4.length()}</ItemCount>${var5}</root>`;
    addToContext(cx, "ListInbound", var6);
}

function activityExtension_4(Context cx) returns error? {
    xml var0 = getFromContext(cx, "FetchOrders-input");
    xml var1 = check xml:fromString(string `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://tns.tibco.com/bw/activity/getFile" version="2.0"><xsl:template name="FetchOrders-input" match="/"><tns1:SFTPGetInputDataFile><RemoteFileName><xsl:value-of select="'/inbound/orders.csv'"/></RemoteFileName><LocalFileName><xsl:value-of select="'/work/orders.csv'"/></LocalFileName></tns1:SFTPGetInputDataFile></xsl:template></xsl:stylesheet>`);
    xml var2 = check xslt:transform(var0, var1, cx.variables);
    string var3 = (var2/**/<RemoteFileName>/*).toString();
    string var4 = (var2/**/<LocalFileName>/*).toString();
    xml var5 = check sftpGetFiles(Orders_SftpConnection, var3, var4, true);
    xml var6 = xml `<root>${var5}</root>`;
    addToContext(cx, "FetchOrders", var6);
}

function receiveEvent(Context cx) returns error? {
}

function scopeActivityRunner(Context cx) returns error? {
    check receiveEvent(cx);
    check activityExtension(cx);
    check activityExtension_1(cx);
    check activityExtension_2(cx);
    check activityExtension_3(cx);
    check activityExtension_4(cx);
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

function start_test_sftp_ArchiveProcess(Context params) returns () {
    scopeScopeFn(params);
}

function sftpDeleteFiles(ftp:Client sftpClient, string remotePath) returns error? {
    foreach string path in check sftpRemoteFiles(sftpClient, remotePath) {
        check sftpClient->delete(path);
    }
}

function sftpGetFiles(ftp:Client sftpClient, string remotePath, string localPath,
        boolean overwrite) returns xml|error {
    if localPath == "" {
        return error("SFTP Get without LocalFileName (Use Process Data) is not supported");
    }
    int? remoteSeparator = remotePath.lastIndexOf("/");
    string remoteName = remoteSeparator == () ? remotePath :
        remotePath.substring(remoteSeparator + 1);
    boolean wildcard = remoteName.includes("*") || remoteName.includes("?");
    string localDirectory = localPath;
    if wildcard && !localPath.endsWith("/") && !localPath.endsWith("\\")
            && !check file:test(localPath, file:IS_DIR) {
        int localSeparator = int:max(localPath.lastIndexOf("/") ?: -1,
                localPath.lastIndexOf("\\") ?: -1);
        localDirectory = localSeparator == -1 ? "." : localPath.substring(0, localSeparator);
    }
    string[] remoteFiles = check sftpRemoteFiles(sftpClient, remotePath);
    if wildcard && remoteFiles.length() == 0 {
        return error("GetFilesException: no remote file matches " + remotePath);
    }
    xml transferred = xml ``;
    foreach string remoteFile in remoteFiles {
        string localFile = localPath;
        if wildcard {
            int? separator = remoteFile.lastIndexOf("/");
            string baseName = separator == () ? remoteFile : remoteFile.substring(separator + 1);
            localFile = localDirectory.endsWith("/") || localDirectory.endsWith("\\")
                ? localDirectory + baseName : localDirectory + "/" + baseName;
        }
        if !overwrite && check file:test(localFile, file:EXISTS) {
            return error("Local file already exists: " + localFile);
        }
        byte[] content = check sftpClient->getBytes(remoteFile);
        check io:fileWriteBytes(localFile, content);
        xml nameElement = xml `<Name>${remoteFile}</Name>`;
        xml sizeElement = xml `<NumOfBytes>${content.length()}</NumOfBytes>`;
        transferred += xml `<FileTransferred>${nameElement}${sizeElement}</FileTransferred>`;
    }
    return transferred;
}

function sftpRemoteFiles(ftp:Client sftpClient, string remotePath) returns string[]|error {
    int? separator = remotePath.lastIndexOf("/");
    string fileName = separator == () ? remotePath : remotePath.substring(separator + 1);
    if !fileName.includes("*") && !fileName.includes("?") {
        return [remotePath];
    }
    string directory = separator == () ? "." : separator == 0 ? "/" :
            remotePath.substring(0, separator);
    string pattern = "";
    foreach string ch in fileName {
        if ch == "*" {
            pattern += ".+";
        } else if ch == "?" {
            pattern += ".";
        } else if ".+()[]{}^$|\\".includes(ch) {
            pattern += "\\" + ch;
        } else {
            pattern += ch;
        }
    }
    ftp:FileInfo[] entries = check sftpClient->list(directory);
    string[] matches = [];
    foreach ftp:FileInfo entry in entries {
        if entry.isFile && regex:matches(entry.name, pattern) {
            matches.push(directory == "/" ? "/" + entry.name : directory + "/" + entry.name);
        }
    }
    return matches;
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
