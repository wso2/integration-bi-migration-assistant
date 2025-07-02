public function main() {
    map<SharedVariableContext> jobSharedVariables = {};
    map<xml> paramXML = {};
    Context cx = initContext(paramXML, jobSharedVariables);
    start_main_process(cx);
}
