import ballerina/http;

public listener http:Listener creditcheckservice_Process_listener = new (8080, {host: "localhost"});

service /CreditScore on creditcheckservice_Process_listener {
    resource function post creditscore(Request|xml req) returns Response|http:NotFound|http:InternalServerError|client_404_NotFound {
        Request|error input = tryBindToRequest(req);
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
        return start_creditcheckservice_Process(input, paramXML);
    }
}

service / on creditcheckservice_Process_listener {
    resource function get creditscore() returns Response|http:NotFound|http:InternalServerError {
        xml inputXmlMap = xml `<root></root>`;
        map<xml> paramXML = {get: inputXmlMap};
        return start_creditcheckservice_Process((), paramXML);
    }
}

xmlns "http://www.tibco.com/bpel/2007/extensions" as tibex;
xmlns "http://www.tibco.com/bw/process/info" as info;
xmlns "http://docs.oasis-open.org/ns/opencsa/sca/200912" as sca;
xmlns "http://docs.oasis-open.org/wsbpel/2.0/process/executable" as bpws;
xmlns "http://www.w3.org/2001/XMLSchema" as xsd;
xmlns "http://ns.tibco.com/bw/property" as tibprop;
xmlns "http://xmlns.example.com/20180831151624PLT" as ns0;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+c04dc831-5560-484a-a8f6-7ad811f4006c" as ns1;
xmlns "/T1535753828744Converted/JsonSchema" as ns4;
xmlns "http://www.tibco.com/pe/EngineTypes" as ns;
xmlns "http://www.tibco.com/pe/WriteToLogActivitySchema" as ns2;
xmlns "http://schemas.tibco.com/bw/plugins/basic/6.0/Exceptions" as ns5;
xmlns "http://www.example.com/namespaces/tns/1535845694732" as ns3;
xmlns "http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+input" as ns0;
xmlns "http://www.tibco.com/namespaces/tnt/plugins/jdbc+21902290-4882-46a2-8795-b85989c9d7c0+input" as ns5;
xmlns "http://www.tibco.com/namespaces/tnt/plugins/jdbc+b75f079e-d363-4c28-9b66-44009f6eacf8+output" as ns1;
xmlns "http://schemas.tibco.com/bw/plugins/basic/6.0/Exceptions" as ns8;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.JDBCQuery" as ns2;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.update" as ns7;
xmlns "http://www.tibco.com/namespaces/tnt/plugins/jdbc" as ns6;
