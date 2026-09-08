import ballerina/http;

public listener http:Listener GeneralConnection = new (9090, {host: "localhost"});

service on GeneralConnection {
    resource function 'default [string... path](xml input) returns xml {
        map<SharedVariableContext> jobSharedVariables = {};
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
