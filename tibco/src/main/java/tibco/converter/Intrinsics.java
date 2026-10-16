/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */

package tibco.converter;

public enum Intrinsics {
    GET_FILES_IN_PATH(
            "filesInPath",
            """
                    function getFileName(string absPath) returns string {
                        int? index = absPath.lastIndexOf("/");
                        if index == () {
                            return absPath;
                        }
                        return absPath.substring(index + 1, absPath.length());
                    }

                    function wildcardToRegex(string pattern) returns string {
                        string regexPattern = "";
                        foreach string:Char c in pattern {
                            if c == "*" {
                                regexPattern += ".*";
                            } else if "\\\\^$.|?+()[]{}".includes(c) {
                                regexPattern += "\\\\" + c;
                            } else {
                                regexPattern += c;
                            }
                        }
                        return regexPattern;
                    }

                    function filesInPath(string path, boolean includeFiles, boolean includeDirs)
                            returns FileData[]|error {
                        string basePath = path;
                        string? pattern = ();
                        if path.includes("*") {
                            int? index = path.lastIndexOf("/");
                            if index == () {
                                basePath = ".";
                                pattern = wildcardToRegex(path);
                            } else {
                                basePath = path.substring(0, index);
                                pattern = wildcardToRegex(path.substring(index + 1, path.length()));
                            }
                        } else {
                            file:MetaData metaData = check file:getMetaData(path);
                            if !metaData.dir {
                                return includeFiles
                                    ? [{fileName: getFileName(metaData.absPath), fullName: metaData.absPath}]
                                    : [];
                            }
                        }
                        file:MetaData[] entries = check file:readDir(basePath);
                        FileData[] result = [];
                        foreach file:MetaData entry in entries {
                            if entry.dir ? !includeDirs : !includeFiles {
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
                    """

    ),
    // TIBCO's SFTP activities accept ? (exactly one character) and * (one or more characters) in file names.
    SFTP_GLOB_TO_REGEX(
            "sftpGlobToRegex",
            """
                    function sftpGlobToRegex(string glob) returns string {
                        string pattern = "";
                        foreach string ch in glob {
                            if ch == "*" {
                                pattern += ".+";
                            } else if ch == "?" {
                                pattern += ".";
                            } else if ".+()[]{}^$|\\\\".includes(ch) {
                                pattern += "\\\\" + ch;
                            } else {
                                pattern += ch;
                            }
                        }
                        return pattern;
                    }
                    """
    ),
    // ftp:Client does not understand wildcards, so wildcard names are resolved against a listing of the parent
    // directory.
    SFTP_REMOTE_FILES(
            "sftpRemoteFiles",
            """
                    function sftpRemoteFiles(ftp:Client sftpClient, string remotePath) returns string[]|error {
                        int? separator = remotePath.lastIndexOf("/");
                        string fileName = separator == () ? remotePath : remotePath.substring(separator + 1);
                        if !fileName.includes("*") && !fileName.includes("?") {
                            return [remotePath];
                        }
                        string directory = separator == () ? "." : separator == 0 ? "/" :
                                remotePath.substring(0, separator);
                        string pattern = sftpGlobToRegex(fileName);
                        ftp:FileInfo[] entries = check sftpClient->list(directory);
                        string[] matches = [];
                        foreach ftp:FileInfo entry in entries {
                            if entry.isFile && regex:matches(entry.name, pattern) {
                                matches.push(directory == "/" ? "/" + entry.name : directory + "/" + entry.name);
                            }
                        }
                        return matches;
                    }
                    """
    ),
    SFTP_DELETE_FILES(
            "sftpDeleteFiles",
            """
                    function sftpDeleteFiles(ftp:Client sftpClient, string remotePath) returns error? {
                        foreach string path in check sftpRemoteFiles(sftpClient, remotePath) {
                            check sftpClient->delete(path);
                        }
                    }
                    """
    ),
    // Several matched remote files keep their own names inside the local directory, as TIBCO does; a single file
    // is written to LocalFileName as given.
    SFTP_GET_FILES(
            "sftpGetFiles",
            """
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
                        if wildcard && !localPath.endsWith("/") && !localPath.endsWith("\\\\")
                                && !check file:test(localPath, file:IS_DIR) {
                            int localSeparator = int:max(localPath.lastIndexOf("/") ?: -1,
                                    localPath.lastIndexOf("\\\\") ?: -1);
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
                                localFile = localDirectory.endsWith("/") || localDirectory.endsWith("\\\\")
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
                    """
    ),
    // Several matched local files keep their own names inside RemoteFileName, which must then be an existing remote
    // directory (or end with "/") so that a mistyped path cannot silently redirect the upload; a single file is
    // written to RemoteFileName as given.
    SFTP_PUT_FILES(
            "sftpPutFiles",
            """
                    function sftpPutFiles(ftp:Client sftpClient, string localPath, string remotePath,
                            boolean overwrite, boolean append) returns xml|error {
                        if localPath == "" {
                            return error("SFTP Put without LocalFileName (Use Process Data) is not supported");
                        }
                        int localSeparator = int:max(localPath.lastIndexOf("/") ?: -1,
                                localPath.lastIndexOf("\\\\") ?: -1);
                        string localName = localPath.substring(localSeparator + 1);
                        boolean wildcard = localName.includes("*") || localName.includes("?");
                        string[] localFiles = [localPath];
                        string remoteDirectory = remotePath;
                        if wildcard {
                            string localDirectory = localSeparator == -1 ? "." : localPath.substring(0, localSeparator);
                            string pattern = sftpGlobToRegex(localName);
                            localFiles = [];
                            foreach file:MetaData entry in check file:readDir(localDirectory) {
                                if !entry.dir && regex:matches(check file:basename(entry.absPath), pattern) {
                                    localFiles.push(entry.absPath);
                                }
                            }
                            if localFiles.length() == 0 {
                                return error("PutFilesException: no local file matches " + localPath);
                            }
                            boolean remoteIsDirectory = remotePath.endsWith("/");
                            if !remoteIsDirectory {
                                boolean remoteExists = check sftpClient->exists(remotePath);
                                if remoteExists {
                                    remoteIsDirectory = check sftpClient->isDirectory(remotePath);
                                }
                            }
                            if !remoteIsDirectory {
                                return error("PutFilesException: RemoteFileName must be an existing remote "
                                        + "directory when LocalFileName has wildcards: " + remotePath);
                            }
                        }
                        ftp:FileWriteOption writeOption = !overwrite && append ? ftp:APPEND : ftp:OVERWRITE;
                        xml transferred = xml ``;
                        foreach string localFile in localFiles {
                            string remoteFile = remotePath;
                            if wildcard {
                                string baseName = check file:basename(localFile);
                                remoteFile = remoteDirectory.endsWith("/") ? remoteDirectory + baseName :
                                        remoteDirectory + "/" + baseName;
                            }
                            if !overwrite && !append {
                                boolean remoteExists = check sftpClient->exists(remoteFile);
                                if remoteExists {
                                    return error("PutFilesException: remote file already exists: " + remoteFile);
                                }
                            }
                            byte[] content = check io:fileReadBytes(localFile);
                            check sftpClient->putBytes(remoteFile, content, writeOption);
                            xml nameElement = xml `<Name>${remoteFile}</Name>`;
                            xml sizeElement = xml `<NumOfBytes>${content.length()}</NumOfBytes>`;
                            transferred += xml `<FileTransferred>${nameElement}${sizeElement}</FileTransferred>`;
                        }
                        return transferred;
                    }
                    """
    ),
    SET_SHARED_VARIABLE(
            "setSharedVariable",
            """
                    function setSharedVariable(Context cx, string varName, xml value) {
                        SharedVariableContext varContext = cx.sharedVariables.get(varName);
                        function(xml) setter = varContext.setter;
                        setter(value);
                    }
                    """),
    GET_SHARED_VARIABLE(
            "getSharedVariable",
            """
                    function getSharedVariable(Context cx, string varName) returns xml{
                        SharedVariableContext varContext = cx.sharedVariables.get(varName);
                        function() returns xml getter = varContext.getter;
                        return getter();
                    }
                    """),
    XPATH_PREDICATE(
            "test",
            """
                    function test(xml input, string xpath) returns boolean {
                        // TODO: support XPath
                        return false;
                    }
                    """
    ),
    XML_PARSER_RESULT(
            "XMLElementParseResult",
            """
                    type XMLElementParseResult record {|
                        string? namespace;
                        string name;
                    |};
                    """
    ),
    XML_PARSER(
            "",
            """
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
                    """
    ),
    TO_JSON("xmlToJson",
            """
                    function xmlToJson(xml value) returns json {
                        json result = toJsonInner(value);
                        if (result is map<json> && result.hasKey("InputElement")) {
                            return result.get("InputElement");
                        } else {
                            return result;
                        }
                    }

                    function toJsonInner(xml value) returns json {
                        json result;
                        if (value is xml:Element) {
                            result = toJsonElement(value);
                        } else {
                            result = value.toJson();
                        }
                        return result;
                    }

                    function toJsonElement(xml:Element element) returns json {
                        XMLElementParseResult parseResult = parseElement(element);
                        string name = parseResult.name;

                        xml children = element/*;
                        map<json> body = {};
                        map<json> result = {};
                        foreach xml child in children {
                            json r = toJsonInner(child);
                            if child !is xml:Element {
                                result[name] = r;
                                return result;
                            }
                            string childName = parseElement(child).name;
                            if r !is map<json> {
                                panic error("unexpected");
                            } else {
                                r = r.get(childName);
                            }
                            if body.hasKey(childName) {
                                json current = body.get(childName);
                                if current !is json[] {
                                    json[] n = [body.get(childName)];
                                    n.push(r);
                                    body[childName] = n;
                                } else {
                                    current.push(r);
                                }
                            } else {
                                body[childName] = r;
                            }
                        }
                        result[name] = body;
                        return result;
                    }
                    """),
    RENDER_JSON(
            "renderJson",
            """
                    function renderJson(xml value) returns xml {
                        json jsonValue = xmlToJson(value);
                        return xml `<root><jsonString>${jsonValue.toJsonString()}</jsonString></root>`;
                    }
                    """
    ),
    PATCH_XML_NAMESPACES(
            "transform",
            """
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
                    """

    ),
    RENDER_JSON_AS_XML(
            "renderJSONAsXML",
            """
                    function renderJSONAsXML(json value, string? namespace, string? typeName) returns xml|error {
                        anydata body;
                        if (value is map<json>) {
                            xml acum = xml ``;
                            foreach string key in value.keys() {
                                acum += check renderJSONAsXML(value.get(key), namespace, key);
                            }
                            body = acum;
                        } else {
                            body = value;
                        }

                        string rep = typeName == () ? body.toString() :
                            string `<${typeName}>${body.toString()}</${typeName}>`;
                        xml result = check xml:fromString(rep);
                        if (namespace == ()) {
                            return result;
                        }
                        if (result !is xml:Element) {
                            panic error("Expected XML element");
                        }
                        map<string> attributes = result.getAttributes();
                        attributes["xmlns"] = namespace;
                        return result;
                    }
                    """
    ),
    TRANSFORM_XSLT(
            "transformXSLT",
            """
                    function transformXSLT(xml input) returns xml {
                        xmlns "http://www.w3.org/1999/XSL/Transform" as xsl;
                        xml<xml:Element> values = input/**/<xsl:value\\-of>;
                        foreach xml:Element item in values {
                            map<string> attributes = item.getAttributes();
                            string selectPath = attributes.get("select");
                            int? index = selectPath.indexOf("/");
                            string path;
                            if index == () {
                                path = selectPath;
                            } else {
                                path = selectPath.substring(0, index) + "/" + selectPath.substring(index);
                            }
                            attributes["select"] = path;
                        }
                        xml<xml:Element> test = input/**/<xsl:'if>;
                        foreach xml:Element item in test {
                            map<string> attributes = item.getAttributes();
                            string selectPath = attributes.get("test");
                            int? index = selectPath.indexOf("/");
                            string path;
                            if index == () {
                                path = selectPath;
                            } else {
                                path = selectPath.substring(0, index) + "/" + selectPath.substring(index);
                            }
                            attributes["test"] = path;
                        }
                        return input;
                    }
                    """
    ),
            PARSE_HEADERS(
            "parseHeaders",
            """
                    function parseHeaders(xml headers) returns map<string> {
                        map<string> headerMap = {};
                        foreach xml header in headers {
                            if header is xml:Element {
                                string fullName = header.getName();
                                int? lastIndex = fullName.lastIndexOf("}");
                                string headerName = lastIndex is int ? fullName.substring(lastIndex + 1) : fullName;
                                string headerValue = header.data();
                                headerMap[headerName] = headerValue;
                            }
                        }
                        return headerMap;
                    }
                    """
    ),
    PSG_LOG(
            "psgLog",
            """
                    // TIBCO's `bw.psglog.Log` is a custom (non-stock) logging activity, so this mapping
                    // (Level -> Ballerina log severity) is inferred from observed usage in the source project,
                    // not a spec. Review and adjust as needed.
                    function psgLog(string level, string targetSystem, xml message, xml<xml:Element> keyValuePairs) {
                        xmlns "http://www.tibco.com/PSGLogActivities" as psglog;
                        log:KeyValues keyValues = {};
                        foreach xml:Element pair in keyValuePairs {
                            string paramKey = (pair/**/<psglog:key>/*).toString().trim();
                            string paramValue = (pair/**/<psglog:value>/*).toString().trim();
                            keyValues[paramKey] = paramValue;
                        }
                        keyValues["targetSystem"] = targetSystem;
                        match level {
                            "Warning" => {
                                log:printWarn(message.toString(), keyValues = keyValues);
                            }
                            "Error" => {
                                log:printError(message.toString(), keyValues = keyValues);
                            }
                            "Debug" => {
                                log:printDebug(message.toString(), keyValues = keyValues);
                            }
                            _ => {
                                log:printInfo(message.toString(), keyValues = keyValues);
                            }
                        }
                    }
                    """
    ),
    PSG_SET_AND_LOG(
            "psgSetAndLog",
            """
                    // TIBCO's `bw.psglog.SetAndLog` is a custom (non-stock) logging activity, so this mapping
                    // (Level -> Ballerina log severity) is inferred from observed usage in the source project,
                    // not a spec. Review and adjust as needed.
                    function psgSetAndLog(string level, string targetSystem, xml message, string sessionId,
                            string correlationId, string trackingId, string sender, string serviceScope) {
                        log:KeyValues keyValues = {
                            "targetSystem": targetSystem,
                            "sessionId": sessionId,
                            "correlationId": correlationId,
                            "trackingId": trackingId,
                            "sender": sender,
                            "serviceScope": serviceScope
                        };
                        match level {
                            "Warning" => {
                                log:printWarn(message.toString(), keyValues = keyValues);
                            }
                            "Error" => {
                                log:printError(message.toString(), keyValues = keyValues);
                            }
                            "Debug" => {
                                log:printDebug(message.toString(), keyValues = keyValues);
                            }
                            _ => {
                                log:printInfo(message.toString(), keyValues = keyValues);
                            }
                        }
                    }
                    """
    ),
    TIMER_LISTENER(
            "TimerListener",
            """
                    // A TIBCO Timer starter fires its process on a schedule. `task:Listener` can't host it because
                    // it requires an `isolated` execute method and the generated process functions aren't isolated,
                    // so this listener schedules a `task:Job` instead; being a listener keeps the program alive.
                    type TimerConfig record {|
                        decimal interval = 1;
                        time:Utc? startTime = ();
                        boolean runOnce = false;
                    |};

                    class ProcessTimerJob {
                        *task:Job;
                        private final function () returns () run;

                        function init(function () returns () run) {
                            self.run = run;
                        }

                        public function execute() {
                            self.run();
                        }
                    }

                    class TimerListener {
                        private final TimerConfig|error config;
                        private final function () returns () run;
                        private task:JobId? jobId = ();

                        // An error is a Timer the converter could not resolve; it fails the program at startup.
                        function init(TimerConfig|error config, function () returns () run) {
                            self.config = config;
                            self.run = run;
                        }

                        public function attach(service object {} s, string[]|string? name = ()) returns error? {
                        }

                        public function detach(service object {} s) returns error? {
                        }

                        public function 'start() returns error? {
                            TimerConfig config = check self.config;
                            ProcessTimerJob job = new (self.run);
                            // TIBCO keeps firing after a failed process instance.
                            task:TaskPolicy taskPolicy = {errorPolicy: task:LOG_AND_CONTINUE};
                            // TIBCO fires every second when the interval is shorter than one second.
                            decimal interval = decimal:max(config.interval, 1);
                            time:Utc? startTime = config.startTime;
                            time:Utc? fireTime = startTime is ()
                                ? ()
                                : firstTimerFireTime(startTime, config.runOnce ? () : interval);
                            if config.runOnce {
                                self.jobId = fireTime is ()
                                    ? check task:scheduleJobRecurByFrequency(job, 1, maxCount = 1,
                                            taskPolicy = taskPolicy)
                                    : check task:scheduleOneTimeJob(job, time:utcToCivil(fireTime));
                                return;
                            }
                            self.jobId = fireTime is ()
                                ? check task:scheduleJobRecurByFrequency(job, interval, taskPolicy = taskPolicy)
                                : check task:scheduleJobRecurByFrequency(job, interval,
                                        startTime = time:utcToCivil(fireTime), taskPolicy = taskPolicy);
                        }

                        public function gracefulStop() returns error? {
                            task:JobId? jobId = self.jobId;
                            if jobId is task:JobId {
                                check task:unscheduleJob(jobId);
                            }
                        }

                        public function immediateStop() returns error? {
                            return self.gracefulStop();
                        }
                    }

                    // A start time in the past anchors a recurring schedule: TIBCO projects set an old date to mean
                    // "at this time of day", so the first run is the next start + n * interval after now. The
                    // scheduler rejects a start time that has passed by the time the job is registered, so a fire
                    // time less than a second away runs immediately instead (returned as nil).
                    function firstTimerFireTime(time:Utc startTime, decimal? interval) returns time:Utc? {
                        decimal elapsed = time:utcDiffSeconds(time:utcNow(), startTime);
                        time:Utc fireTime = interval is () || elapsed < 0d
                            ? startTime
                            : time:utcAddSeconds(startTime, ((elapsed / interval).floor() + 1) * interval);
                        return time:utcDiffSeconds(fireTime, time:utcNow()) < 1d ? () : fireTime;
                    }
                    """
    ),
    TIMER_START_TIME(
            "timerStartTime",
            """
                    // Accepts epoch milliseconds, an ISO-8601 timestamp, or TIBCO's "yyyy MMM dd HH:mm:ss z"
                    // property format. TIBCO uses the machine's time zone when the start time has none.
                    function timerStartTime(string value) returns time:Utc?|error {
                        string trimmed = value.trim();
                        if trimmed == "" {
                            return ();
                        }
                        int|error epochMillis = int:fromString(trimmed);
                        if epochMillis is int {
                            return [epochMillis / 1000, <decimal>(epochMillis % 1000) / 1000];
                        }
                        if re `\\d{4}-.*`.isFullMatch(trimmed) {
                            // RFC 3339 needs a "+hh:mm" offset, but TIBCO also writes "+hhmm".
                            if re `.*[+-]\\d{4}`.isFullMatch(trimmed) {
                                int split = trimmed.length() - 2;
                                return time:utcFromString(trimmed.substring(0, split) + ":"
                                        + trimmed.substring(split));
                            }
                            if re `.*(Z|[+-]\\d{2}:\\d{2})`.isFullMatch(trimmed) {
                                return time:utcFromString(trimmed);
                            }
                            time:Zone? systemZone = timerSystemZone();
                            return systemZone is ()
                                ? time:utcFromString(trimmed + "Z")
                                : timerZoneToUtc(systemZone, check timerLocalCivil(trimmed));
                        }
                        string[] parts = re ` +`.split(trimmed);
                        string[] months = ["Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov",
                            "Dec"];
                        int? month = parts.length() == 5 ? months.indexOf(parts[1]) : ();
                        if month is () {
                            return error("Unsupported timer start time: " + value);
                        }
                        string day = parts[2].length() == 1 ? "0" + parts[2] : parts[2];
                        string localTime = string `${parts[0]}-${timerPad(month + 1)}-${day}T${parts[3]}`;
                        string zone = parts[4];
                        if zone == "UTC" || zone == "GMT" || zone == "Z" {
                            return time:utcFromString(localTime + "Z");
                        }
                        if re `[+-]\\d{2}:?\\d{2}`.isFullMatch(zone) {
                            return timerStartTime(localTime + zone);
                        }
                        // getZone panics on an unknown zone ID instead of returning nil.
                        time:Zone?|error namedZone = trap time:getZone(zone);
                        if namedZone !is time:Zone {
                            return error("Unknown time zone in timer start time: " + value);
                        }
                        return timerZoneToUtc(namedZone, check timerLocalCivil(localTime));
                    }

                    // A zone converts only a civil time that carries an offset, so the zone's offset at that moment
                    // (which accounts for daylight saving) is looked up first.
                    function timerZoneToUtc(time:Zone zone, time:Civil local) returns time:Utc|error {
                        time:Utc asUtc = check time:utcFromCivil({year: local.year, month: local.month, day: local.day,
                            hour: local.hour, minute: local.minute, second: local.second, utcOffset: {hours: 0}});
                        return time:utcFromCivil({year: local.year, month: local.month, day: local.day,
                            hour: local.hour, minute: local.minute, second: local.second,
                            utcOffset: zone.utcToCivil(asUtc).utcOffset ?: {hours: 0}});
                    }

                    function timerLocalCivil(string localTime) returns time:Civil|error {
                        string[] dateAndTime = re `T`.split(localTime);
                        string[] date = dateAndTime.length() == 2 ? re `-`.split(dateAndTime[0]) : [];
                        string[] clock = dateAndTime.length() == 2 ? re `:`.split(dateAndTime[1]) : [];
                        if date.length() != 3 || clock.length() < 2 {
                            return error("Unsupported timer start time: " + localTime);
                        }
                        return {
                            year: check int:fromString(date[0]),
                            month: check int:fromString(date[1]),
                            day: check int:fromString(date[2]),
                            hour: check int:fromString(clock[0]),
                            minute: check int:fromString(clock[1]),
                            second: clock.length() > 2 ? check decimal:fromString(clock[2]) : 0
                        };
                    }
                    """
    ),
    TIMER_OUTPUT(
            "timerOutput",
            """
                    // The fields of TIBCO's TimerOutputSchema in the machine's time zone, wrapped in the `root` that
                    // stands for the output element, so mappings such as $Timer/Hour resolve. Week is the week of
                    // the year counted from 1 January, and Time is assumed to be formatted as HH:mm:ss.
                    function timerOutput() returns xml {
                        time:Utc now = time:utcNow();
                        time:Zone? zone = timerSystemZone();
                        time:Civil civil = zone is () ? time:utcToCivil(now) : zone.utcToCivil(now);
                        int second = <int>(civil.second ?: 0d).floor();
                        int[] daysBeforeMonth = [0, 31, 59, 90, 120, 151, 181, 212, 243, 273, 304, 334];
                        boolean leapYear = civil.year % 4 == 0 && (civil.year % 100 != 0 || civil.year % 400 == 0);
                        int dayOfYear = daysBeforeMonth[civil.month - 1] + civil.day - 1
                            + (leapYear && civil.month > 2 ? 1 : 0);
                        int nowMillis = now[0] * 1000 + <int>(now[1] * 1000).floor();
                        string date = string `${civil.year}-${timerPad(civil.month)}-${timerPad(civil.day)}`;
                        string clock = string `${timerPad(civil.hour)}:${timerPad(civil.minute)}:${timerPad(second)}`;
                        xml fields = xml `<Now>${nowMillis}</Now><Hour>${civil.hour}</Hour>`
                            + xml `<Minute>${civil.minute}</Minute><Second>${second}</Second>`
                            + xml `<Week>${dayOfYear / 7 + 1}</Week><Month>${civil.month}</Month>`
                            + xml `<Year>${civil.year}</Year><Date>${date}</Date><Time>${clock}</Time>`
                            + xml `<DayOfMonth>${civil.day}</DayOfMonth>`;
                        return xml `<root>${fields}</root>`;
                    }
                    """
    ),
    TIMER_TIME_UTILS(
            "timerSystemZone",
            """
                    // Some Ballerina runtimes fail to load the system zone; UTC is then used, which is also the
                    // usual zone of a container.
                    function timerSystemZone() returns time:Zone? {
                        time:Zone|error zone = time:loadSystemZone();
                        return zone is time:Zone ? zone : ();
                    }

                    function timerPad(int value) returns string {
                        return value < 10 ? "0" + value.toString() : value.toString();
                    }
                    """
    ),
    PSG_EXCEPTION_LOG(
            "psgExceptionLog",
            """
                    // TIBCO's `bw.psglog.ExceptionLog` reads fault details (errorCode/errorMessage/processStack/
                    // stackTrace) off the active fault variable. There is no live Ballerina `error` in scope at
                    // this point, so one is synthesized here purely to carry these fields into `log:printError`.
                    function psgExceptionLog(string errorCode, string errorMessage, string processStack,
                            string stackTrace) {
                        error psgError = error(errorMessage, code = errorCode, processStack = processStack,
                                stackTrace = stackTrace);
                        // `stackTrace` is a reserved log:printError parameter name (error:StackFrame[]?), so the
                        // TIBCO stack-trace text is logged under `tibcoStackTrace` to avoid the collision.
                        log:printError(errorMessage, 'error = psgError, code = errorCode,
                                processStack = processStack, tibcoStackTrace = stackTrace);
                    }
                    """
    );
    public final String body;
    public final String name;

    Intrinsics(String name, String body) {
        this.name = name;
        this.body = body;
    }
}
