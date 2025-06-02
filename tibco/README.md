## Supported TIBCO BusinessWorks activities

- `invoke`
- `pick`
- `empty`
- `reply`
- `throw`
- `assign`
- `forEach`
- `extensionActivity`
  - `receiveEvent`
  - `activityExtension`
    - `bw.internal.end`
    - `bw.http.sendHTTPRequest`
    - `bw.restjson.JsonRender`
    - `bw.restjson.JsonParser`
    - `bw.http.sendHTTPResponse`
    - `bw.file.write`
    - `bw.generalactivities.log`
    - `bw.xml.renderxml`
    - `bw.generalactivities.mapper`
    - `bw.internal.accumulateend`
  - `extActivity`
- `com.tibco.plugin.mapper.MapperActivity`
- `com.tibco.plugin.http.HTTPEventSource`
- `com.tibco.pe.core.AssignActivity`
- `com.tibco.plugin.http.HTTPResponseActivity`
- `com.tibco.plugin.xml.XMLRendererActivity`
- `com.tibco.plugin.xml.XMLParseActivity`
- `com.tibco.pe.core.LoopGroup`
- `com.tibco.pe.core.WriteToLogActivity`
- `com.tibco.pe.core.CatchActivity`
- `com.tibco.plugin.file.FileReadActivity`
- `com.tibco.plugin.file.FileWriteActivity`
- `com.tibco.plugin.jdbc.JDBCGeneralActivity`
- `com.tibco.plugin.json.activities.RestActivity`
- `com.tibco.pe.core.CallProcessActivity`
- `com.tibco.plugin.soap.SOAPSendReceiveActivity`
- `com.tibco.plugin.json.activities.JSONParserActivity`
- `com.tibco.plugin.json.activities.JSONRenderActivity`
- `com.tibco.plugin.soap.SOAPSendReplyActivity`
- `com.tibco.pe.core.WriteToLogActivity`

## Sample conversion projects

See `tibco/src/test/resources/tibco.projects` for sample BW projects. Corresponding Ballerina projects are in `tibco/src/test/resources/tibco.projects.converted`
