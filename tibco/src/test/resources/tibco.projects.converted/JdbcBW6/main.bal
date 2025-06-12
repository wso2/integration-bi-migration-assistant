import ballerina/http;

public listener http:Listener test_api_MainProcess_listener = new (8080, {host: "localhost"});

service /TestAPI on test_api_MainProcess_listener {
    resource function post test(TestRequest|xml req) returns TestResponse|http:NotFound|http:InternalServerError {
        TestRequest|error input = tryBindToTestRequest(req);
        if input is error {
            return <http:InternalServerError>{};
        }
        xml inputValXml = checkpanic toXML(input);
        xml extractedBody = inputValXml/*;
        xml inputXml = xml `<item>
    ${extractedBody}
</item>`;
        xml inputXmlMap = xml `<root>${inputXml}</root>`;
        map<xml> paramXML = {post: inputXmlMap};
        return start_test_api_MainProcess(initContext(paramXML));
    }
}

xmlns "http://xmlns.example.com/test/api/wsdl" as ns0;
xmlns "http://www.tibco.com/bpel/2007/extensions" as tibex;
xmlns "http://www.tibco.com/pe/EngineTypes" as ns;
xmlns "http://www.tibco.com/bw/process/info" as info;
xmlns "http://docs.oasis-open.org/ns/opencsa/sca/200912" as sca;
xmlns "http://docs.oasis-open.org/wsbpel/2.0/process/executable" as bpws;
xmlns "http://www.w3.org/2001/XMLSchema" as xsd;
xmlns "http://ns.tibco.com/bw/property" as tibprop;
xmlns "http://xmlns.example.com/test/api" as ns1;
