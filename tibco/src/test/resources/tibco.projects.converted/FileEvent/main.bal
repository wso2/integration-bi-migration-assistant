import ballerina/file;

public listener file:Listener File_PollerListener = new (
    path = "./TestDir/",
    recursive = true
);

service "File_Poller" on File_PollerListener {
    remote function onCreate(file:FileEvent event) {
        map<SharedVariableContext> jobSharedVariables = {};
        // TODO: add any addition data to this XML
        xml data = xml `<fileInfo>
    <fileName>
        ${event.name}
    </fileName>
</fileInfo>`;
        map<xml> paramXML = {file: data};
        Context cx = initContext(paramXML, jobSharedVariables);
        start_main_process(cx);
    }

    remote function onModify(file:FileEvent event) {
        map<SharedVariableContext> jobSharedVariables = {};
        // TODO: add any addition data to this XML
        xml data = xml `<fileInfo>
    <fileName>
        ${event.name}
    </fileName>
</fileInfo>`;
        map<xml> paramXML = {file: data};
        Context cx = initContext(paramXML, jobSharedVariables);
        start_main_process(cx);
    }

    remote function onDelete(file:FileEvent event) {
        map<SharedVariableContext> jobSharedVariables = {};
        // TODO: add any addition data to this XML
        xml data = xml `<fileInfo>
    <fileName>
        ${event.name}
    </fileName>
</fileInfo>`;
        map<xml> paramXML = {file: data};
        Context cx = initContext(paramXML, jobSharedVariables);
        start_main_process(cx);
    }
}

xmlns "http://xmlns.tibco.com/bw/process/2003" as pd;
xmlns "http://www.w3.org/1999/XSL/Transform" as xsl;
xmlns "http://www.tibco.com/pe/WriteToLogActivitySchema" as ns;
xmlns "http://www.tibco.com/namespaces/tnt/plugins/file" as ns1;
