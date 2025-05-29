import ballerina/http;

public listener http:Listener creditapp_module_ExperianScore_listener = new (8080, {host: "localhost"});
public listener http:Listener creditapp_module_EquifaxScore_listener = new (8081, {host: "localhost"});
public listener http:Listener creditapp_module_MainProcess_listener = new (8082, {host: "localhost"});

service / on creditapp_module_ExperianScore_listener {
    resource function post creditscore(GiveNewSchemaNameHere|xml req) returns ExperianResponseSchemaElement|http:NotFound|http:InternalServerError {
        GiveNewSchemaNameHere|error input = tryBindToGiveNewSchemaNameHere(req);
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
        return start_creditapp_module_ExperianScore(input, paramXML);
    }
}

service /y54cuadtcxtfstqs3rux2gfdaxppoqgc on creditapp_module_EquifaxScore_listener {
    resource function post creditscore(GiveNewSchemaNameHere|xml req) returns SuccessSchema|http:NotFound|http:InternalServerError|client_404_RecordNotFound {
        GiveNewSchemaNameHere|error input = tryBindToGiveNewSchemaNameHere(req);
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
        return start_creditapp_module_EquifaxScore(input, paramXML);
    }
}

service / on creditapp_module_EquifaxScore_listener {
    resource function post creditscore(GiveNewSchemaNameHere|xml req) returns SuccessSchema|http:NotFound|http:InternalServerError {
        GiveNewSchemaNameHere|error input = tryBindToGiveNewSchemaNameHere(req);
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
        return start_creditapp_module_EquifaxScore(input, paramXML);
    }
}

service /CreditDetails on creditapp_module_MainProcess_listener {
    resource function post creditdetails(GiveNewSchemaNameHere|xml req) returns CreditScoreSuccessSchema|http:NotFound|http:InternalServerError {
        GiveNewSchemaNameHere|error input = tryBindToGiveNewSchemaNameHere(req);
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
        return start_creditapp_module_MainProcess(input, paramXML);
    }
}

xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+d91c38bb-55d7-465a-ac2a-6a8707a5c797" as ns5;
xmlns "http://tns.tibco.com/bw/json/1535671685533" as ns3;
xmlns "http://www.tibco.com/bpel/2007/extensions" as tibex;
xmlns "activity.jsonParser.input+a3fa07a6-0270-48b7-ba84-7de6924acb3d+ActivityInputType" as ns13;
xmlns "http://www.tibco.com/bw/process/info" as info;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+b128583e-2c2a-4c78-99de-908047fbb8fd" as ns6;
xmlns "http://docs.oasis-open.org/ns/opencsa/sca/200912" as sca;
xmlns "http://xmlns.example.com/20180830120910PLT" as ns0;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+814c5cb3-bfa4-4505-be87-00d7681d7c79" as ns4;
xmlns "http://docs.oasis-open.org/wsbpel/2.0/process/executable" as bpws;
xmlns "http://www.w3.org/2001/XMLSchema" as xsd;
xmlns "http://ns.tibco.com/bw/property" as tibprop;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+954ecebd-35f1-46a3-9409-70b911bee5e7" as ns1;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+bw.restjson.JsonParser" as ns14;
xmlns "http://www.tibco.com/pe/EngineTypes" as ns;
xmlns "http://tns.tibco.com/bw/activity/sendhttprequest/input+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityInput" as ns8;
xmlns "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" as ns7;
xmlns "http://tns.tibco.com/bw/activity/jsonRender/xsd/input/55832ae5-2a37-4b37-8392-a64537f49367" as ns15;
xmlns "http://xmlns.example.com/Creditscore/parameters" as ns2;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+bw.http.sendHTTPRequest" as ns10;
xmlns "http://tns.tibco.com/bw/activity/sendhttprequest/output+255a70f6-2bf4-4f72-928d-3fe2a72ce7a0+RequestActivityOutput" as ns9;
xmlns "activity.jsonRender.output+b4f6a2ce-0fe1-42dd-b664-1220acad7966+ActivityOutputType" as ns11;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+bw.restjson.JsonRender" as ns12;
xmlns "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" as ns2;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+44ece17e-f278-4255-b693-65bb9cf58bca" as ns3;
xmlns "http://tns.tibco.com/bw/palette/internal/activityerror+2632841e-3358-4851-a82f-2f2cd3aeec16" as ns1;
xmlns "http://xmlns.example.com/20180827154353PLT" as ns0;
xmlns "http://xmlns.example.com/20180827160122PLT" as ns0;
xmlns "/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" as ns1;
