import ballerina/http;

public listener http:Listener placeholder_listener = new (8080);

service on placeholder_listener {
    resource function 'default [string... path](xml input) returns xml {
        map<SharedVariableContext> jobSharedVariables = {};
        xml shared = xml `<callData><count>0</count></callData>`;
        SharedVariableContext sharedContext = {getter: function() returns xml {
                return shared;
            }, setter: function(xml value) {
                shared = value;
            }};
        jobSharedVariables["shared"] = sharedContext;
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

service "Log" on {
    remote function onStart() {
        // FIXME: service for start activity: Log

        // FIXME: call start_lib_shared_process from fixed service
    }
}

xmlns "http://xmlns.tibco.com/bw/process/2003" as pd;
xmlns "http://www.w3.org/1999/XSL/Transform" as xsl;
xmlns "http://www.tibco.com/pe/EngineTypes" as ns;
xmlns "http://www.w3.org/2001/XMLSchema" as xsd;
