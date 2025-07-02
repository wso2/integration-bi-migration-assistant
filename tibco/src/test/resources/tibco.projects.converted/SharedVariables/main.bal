import ballerina/http;

xml sharedVariable = xml `<sharedData><count>0</count></sharedData>`;
public listener http:Listener GeneralConnection_sharedhttp = new (9090, {host: "localhost"});

service on GeneralConnection_sharedhttp {
    resource function 'default [string... path](xml input) returns xml {
        map<SharedVariableContext> jobSharedVariables = {};
        xml callVar = xml `<callData><count>0</count></callData>`;
        SharedVariableContext callVarContext = {getter: function() returns xml {
                return callVar;
            }, setter: function(xml value) {
                callVar = value;
            }};
        jobSharedVariables["callVar"] = callVarContext;
        xml inputVal = xml `<root>
    <item>
        ${input}
    </item>
</root>`;
        map<xml> paramXML = {post: inputVal};
        Context cx = initContext(paramXML, jobSharedVariables);
        start_Processes_Main_process(cx);
        xml response = cx.result;
        return response;
    }
}

xmlns "http://xmlns.tibco.com/bw/process/2003" as pd;
xmlns "http://www.w3.org/1999/XSL/Transform" as xsl;
xmlns "http://www.tibco.com/pe/EngineTypes" as ns;
xmlns "http://www.w3.org/2001/XMLSchema" as xsd;
