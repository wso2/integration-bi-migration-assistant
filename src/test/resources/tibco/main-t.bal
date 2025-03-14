import ballerina/data.xmldata;
import ballerina/http;
import ballerina/xslt;

type ErrorReport record {
    string StackTrace;
    string Msg;
    string FullClass;
    string Class;
    string ProcessStack;
    string MsgCode;
    anydata Data;
};

type OptionalErrorReport record {
    string StackTrace;
    string Msg;
    string FullClass;
    string Class;
    string ProcessStack;
    string MsgCode;
    anydata Data;
};

type FaultDetail record {
    string ActivityName;
    anydata Data;
    string Msg;
    string MsgCode;
    string ProcessStack;
    string StackTrace;
    string FullClass;
    string Class;
};

type ProcessContext record {
    string JobId;
    string ApplicationName;
    string EngineName;
    string ProcessInstanceId;
    string CustomJobId;
    string TrackingInfo;
};

type CorrelationValue string;

type ActivityExceptionType record {
    string msg;
    string msgCode;
};

type ActivityTimedOutExceptionType record {
    *ActivityExceptionType;
};

type DuplicateKeyExceptionType record {
    *ActivityExceptionType;
    string duplicateKey;
    string previousJobID;
};

type ActivityException ActivityExceptionType;

type ActivityTimedOutException ActivityTimedOutExceptionType;

type DuplicateKeyException DuplicateKeyExceptionType;

type SuccessSchema record {
    int FICOScore;
    int NoOfInquiries;
    string Rating;
};

type GiveNewSchemaNameHere record {
    string DOB;
    string FirstName;
    string LastName;
    string SSN;
};

type CreditScoreSuccessSchema record {
    SuccessSchema EquifaxResponse;
    SuccessSchema ExperianResponse;
    SuccessSchema TransUnionResponse;
};

listener http:Listener LISTENER = new (8080, {host: "localhost"});

service /CreditDetails on LISTENER {
    resource function post creditdetails(GiveNewSchemaNameHere input) returns CreditScoreSuccessSchema|http:NotFound|http:InternalServerError {
        return creditapp_module_MainProcess_start(input);
    }
}

public function creditapp_module_MainProcess_start(GiveNewSchemaNameHere input) returns CreditScoreSuccessSchema {
    xml inputXML = toXML(input);
    xml xmlResult = process_creditapp_module_MainProcess(inputXML);
    CreditScoreSuccessSchema result = convertToCreditScoreSuccessSchema(xmlResult);
    return result;
}

function empty(xml input) returns xml {
    xml inputXML = input is xml ? input : toXML(input);
    return inputXML;
}

function empty_2(xml input) returns xml {
    xml inputXML = input is xml ? input : toXML(input);
    return inputXML;
}

function extActivity(xml input) returns xml {
    xml inputXML = input is xml ? input : toXML(input);
    xml var0 = checkpanic xslt:transform(inputXML, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0">
    <xsl:param name="post.item"/>
    <xsl:template name="FICOScore-input" match="/">
        <tns:GiveNewSchemaNameHere>
            <xsl:if test="$post.item/tns:DOB">
                <tns:DOB>
                    <xsl:value-of select="$post.item/tns:DOB"/>
                </tns:DOB>
            </xsl:if>
            <xsl:if test="$post.item/tns:FirstName">
                <tns:FirstName>
                    <xsl:value-of select="$post.item/tns:FirstName"/>
                </tns:FirstName>
            </xsl:if>
            <xsl:if test="$post.item/tns:LastName">
                <tns:LastName>
                    <xsl:value-of select="$post.item/tns:LastName"/>
                </tns:LastName>
            </xsl:if>
            <xsl:if test="$post.item/tns:SSN">
                <tns:SSN>
                    <xsl:value-of select="$post.item/tns:SSN"/>
                </tns:SSN>
            </xsl:if>
        </tns:GiveNewSchemaNameHere>
    </xsl:template>
</xsl:stylesheet>`);
    creditapp_module_EquifaxScore_start(var0);
}

function extActivity_5(xml input) returns xml {
    xml inputXML = input is xml ? input : toXML(input);
    xml var0 = checkpanic xslt:transform(inputXML, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns3="http://xmlns.example.com/Creditscore/parameters" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" version="2.0">
    <xsl:param name="post.item"/>
    <xsl:template name="ExperianScore-input" match="/">
        <tns:GiveNewSchemaNameHere>
            <xsl:if test="$post.item/tns:DOB">
                <tns:DOB>
                    <xsl:value-of select="$post.item/tns:DOB"/>
                </tns:DOB>
            </xsl:if>
            <xsl:if test="$post.item/tns:FirstName">
                <tns:FirstName>
                    <xsl:value-of select="$post.item/tns:FirstName"/>
                </tns:FirstName>
            </xsl:if>
            <xsl:if test="$post.item/tns:LastName">
                <tns:LastName>
                    <xsl:value-of select="$post.item/tns:LastName"/>
                </tns:LastName>
            </xsl:if>
            <xsl:if test="$post.item/tns:SSN">
                <tns:SSN>
                    <xsl:value-of select="$post.item/tns:SSN"/>
                </tns:SSN>
            </xsl:if>
        </tns:GiveNewSchemaNameHere>
    </xsl:template>
</xsl:stylesheet>`);
    creditapp_module_ExperianScore_start(var0);
}

function pick(xml input) returns xml {
    xml inputXML = input is xml ? input : toXML(input);
    return inputXML;
}

function process_creditapp_module_MainProcess(xml input) returns xml {
    worker start_worker {
        xml output = reply(input);
        output -> FICOScoreTopostOut;
        output -> ExperianScoreTopostOut;
    }
    worker ExperianScoreTopostOut {
        xml v0 = <- start_worker;
        xml output0 = extActivity_5(v0);
        output0 -> function;
    }
    worker FICOScoreTopostOut {
        xml v0 = <- start_worker;
        xml output0 = extActivity(v0);
        output0 -> function;
    }
    xml result0 = <- ExperianScoreTopostOut;
    xml result1 = <- FICOScoreTopostOut;
    return result0 + result1;
}

function reply(xml input) returns xml {
    xml inputXML = input is xml ? input : toXML(input);
    xml var0 = checkpanic xslt:transform(inputXML, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180827160122PLT" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" xmlns:tns2="http://tns.tibco.com/bw/json/1535671685533" version="2.0">
    <xsl:param name="EquifaxScore"/>
    <xsl:param name="ExperianScore"/>
    <xsl:template name="postOut-input" match="/">
        <tns:CreditScoreSuccessSchema>
            <tns:EquifaxResponse>
                <xsl:if test="$EquifaxScore/tns:FICOScore">
                    <tns:FICOScore>
                        <xsl:value-of select="$EquifaxScore/tns:FICOScore"/>
                    </tns:FICOScore>
                </xsl:if>
                <xsl:if test="$EquifaxScore/tns:NoOfInquiries">
                    <tns:NoOfInquiries>
                        <xsl:value-of select="$EquifaxScore/tns:NoOfInquiries"/>
                    </tns:NoOfInquiries>
                </xsl:if>
                <xsl:if test="$EquifaxScore/tns:Rating">
                    <tns:Rating>
                        <xsl:value-of select="$EquifaxScore/tns:Rating"/>
                    </tns:Rating>
                </xsl:if>
            </tns:EquifaxResponse>
            <tns:ExperianResponse>
                <xsl:if test="$ExperianScore/tns2:fiCOScore">
                    <tns:FICOScore>
                        <xsl:value-of select="$ExperianScore/tns2:fiCOScore"/>
                    </tns:FICOScore>
                </xsl:if>
                <xsl:if test="$ExperianScore/tns2:noOfInquiries">
                    <tns:NoOfInquiries>
                        <xsl:value-of select="$ExperianScore/tns2:noOfInquiries"/>
                    </tns:NoOfInquiries>
                </xsl:if>
                <xsl:if test="$ExperianScore/tns2:rating">
                    <tns:Rating>
                        <xsl:value-of select="$ExperianScore/tns2:rating"/>
                    </tns:Rating>
                </xsl:if>
            </tns:ExperianResponse>
        </tns:CreditScoreSuccessSchema>
    </xsl:template>
</xsl:stylesheet>`);
    xml var1 = checkpanic xslt:transform(var0, xml `<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:tns1="http://xmlns.example.com/20180827160122PLT" xmlns:tns="/y54cuadtcxtfstqs3rux2gfdaxppoqgc/T1535409245354Converted/JsonSchema" xmlns:tns2="http://tns.tibco.com/bw/json/1535671685533" version="2.0"><xsl:param name="EquifaxScore"/><xsl:param name="ExperianScore"/><xsl:template name="postOut-input" match="/"><tns1:postResponse><item><tns:CreditScoreSuccessSchema><tns:EquifaxResponse><xsl:if test="$EquifaxScore/tns:FICOScore"><tns:FICOScore><xsl:value-of select="$EquifaxScore/tns:FICOScore"/></tns:FICOScore></xsl:if><xsl:if test="$EquifaxScore/tns:NoOfInquiries"><tns:NoOfInquiries><xsl:value-of select="$EquifaxScore/tns:NoOfInquiries"/></tns:NoOfInquiries></xsl:if><xsl:if test="$EquifaxScore/tns:Rating"><tns:Rating><xsl:value-of select="$EquifaxScore/tns:Rating"/></tns:Rating></xsl:if></tns:EquifaxResponse><tns:ExperianResponse><xsl:if test="$ExperianScore/tns2:fiCOScore"><tns:FICOScore><xsl:value-of select="$ExperianScore/tns2:fiCOScore"/></tns:FICOScore></xsl:if><xsl:if test="$ExperianScore/tns2:noOfInquiries"><tns:NoOfInquiries><xsl:value-of select="$ExperianScore/tns2:noOfInquiries"/></tns:NoOfInquiries></xsl:if><xsl:if test="$ExperianScore/tns2:rating"><tns:Rating><xsl:value-of select="$ExperianScore/tns2:rating"/></tns:Rating></xsl:if></tns:ExperianResponse></tns:CreditScoreSuccessSchema></item></tns1:postResponse></xsl:template></xsl:stylesheet>`);
    return var1;
}

function toXML(map<anydata> data) returns xml {
    return checkpanic xmldata:toXml(data);
}

function convertToCreditScoreSuccessSchema(xml input) returns CreditScoreSuccessSchema {
    return checkpanic xmldata:parseAsType(input);
}
