import ballerina/http;

public listener http:Listener experianservice_module_Process_listener = new (8080, {host: "localhost"});

service /Creditscore on experianservice_module_Process_listener {
    resource function post creditscore(InputElement|xml req) returns ExperianResponseSchemaElement|http:NotFound|http:InternalServerError {
        InputElement|error input = tryBindToInputElement(req);
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
        return start_experianservice_module_Process(input, paramXML);
    }
}

xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+bw.restjson.JsonRender" as ns10;
xmlns "http://www.tibco.com/namespaces/tnt/plugins/httpreceiver+c9689e27-ed49-43a7-9902-684c436e3a8a+ActivityOutputType" as ns1;
xmlns "http://xmlns.example.com/20180902175743PLT" as ns0;
xmlns "http://www.tibco.com/bpel/2007/extensions" as tibex;
xmlns "http://www.tibco.com/bw/process/info" as info;
xmlns "http://docs.oasis-open.org/ns/opencsa/sca/200912" as sca;
xmlns "http://tns.tibco.com/bw/activity/sendhttpresponse/xsd/input+3847aa9b-8275-4b15-9ea8-812816768fa4+ResponseActivityInput" as ns2;
xmlns "http://docs.oasis-open.org/wsbpel/2.0/process/executable" as bpws;
xmlns "http://www.w3.org/2001/XMLSchema" as xsd;
xmlns "activity.jsonRender.output+8ccea717-63a9-4d35-945d-ec9437e37100+ActivityOutputType" as ns9;
xmlns "http://ns.tibco.com/bw/property" as tibprop;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+bw.jdbc.JDBCQuery" as ns6;
xmlns "http://www.tibco.com/pe/EngineTypes" as ns;
xmlns "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+input" as ns4;
xmlns "activity.jsonParser.input+f396d921-0bc7-459d-ae81-0eb1a0f94723+ActivityInputType" as ns7;
xmlns "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367" as ns11;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+bw.http.sendHTTPResponse" as ns3;
xmlns "http://tns.tibco.com/bw/json/1535671685533" as ns12;
xmlns "http://www.tibco.com/namespaces/tnt/plugins/jdbc+1d5225ab-4bc8-4898-8f74-01e4317c3e29+output" as ns5;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+bw.restjson.JsonParser" as ns8;
