import ballerina/http;

public listener http:Listener GeneralConnection = new (9090, {host: "localhost"});
public listener http:Listener placeholder_listener = new (8080);

service on placeholder_listener {
    resource function 'default [string... path](xml input) returns xml {
        map<SharedVariableContext> jobSharedVariables = {};
        xml inputVal = xml `<root>
    <item>
        ${input}
    </item>
</root>`;
        map<xml> paramXML = {post: inputVal};
        Context cx = initContext(paramXML, jobSharedVariables);
        start_Processes_MainProcessStarter_process(cx);
        xml response = cx.result;
        return response;
    }
}

service on placeholder_listener {
    resource function 'default [string... path](xml input) returns xml {
        map<SharedVariableContext> jobSharedVariables = {};
        xml inputVal = xml `<root>
    <item>
        ${input}
    </item>
</root>`;
        map<xml> paramXML = {post: inputVal};
        Context cx = initContext(paramXML, jobSharedVariables);
        start_Processes_Other_process(cx);
        xml response = cx.result;
        return response;
    }
}

xmlns "http://xmlns.tibco.com/bw/process/2003" as pd;
xmlns "http://www.w3.org/1999/XSL/Transform" as xsl;
xmlns "http://www.tibco.com/pe/EngineTypes" as ns;
xmlns "http://www.w3.org/2001/XMLSchema" as xsd;
xmlns "http://www.tibco.com/namespaces/tnt/plugins/json" as ns1;
