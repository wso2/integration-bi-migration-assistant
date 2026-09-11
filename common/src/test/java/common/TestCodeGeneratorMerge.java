/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com).
 *
 *  WSO2 LLC. licenses this file to you under the Apache License,
 *  Version 2.0 (the "License"); you may not use this file except
 *  in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing,
 *  software distributed under the License is distributed on an
 *  "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 *  KIND, either express or implied.  See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package common;

import io.ballerina.compiler.syntax.tree.ModulePartNode;
import io.ballerina.compiler.syntax.tree.NodeParser;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import io.ballerina.tools.text.TextDocuments;
import org.testng.Assert;
import org.testng.annotations.Test;

public class TestCodeGeneratorMerge {

    @Test
    public void testMergeRenamesCollidingType() {
        SyntaxTree boilerplateTypes = syntaxTreeFrom("""
                public type Response record {|
                    "JSONResponse"|"XMLResponse"|"TextResponse" kind;
                    anydata payload;
                |};
                """);
        SyntaxTree xsdDerivedTypes = syntaxTreeFrom("""
                import ballerina/data.xmldata;

                @xmldata:Namespace {uri: "http://example.com/CommonResponse"}
                public type Response record {|
                    @xmldata:Element {minOccurs: 1, maxOccurs: 1}
                    string ResponseCode;
                |};

                public type Wrapper record {|
                    Response response;
                    Response[] responses;
                |};
                """);

        String merged = CodeGenerator.merge(boilerplateTypes, xsdDerivedTypes).toSourceCode();

        Assert.assertTrue(merged.contains("public type Response record"),
                "the boilerplate Response type should survive unrenamed since it wins the name collision");
        Assert.assertTrue(merged.contains("public type Response1 record"),
                "the colliding xsd-derived Response type should be renamed to Response1");
        Assert.assertTrue(merged.contains("@xmldata:Name {value: \"Response\"}"),
                "the renamed type should carry an @xmldata:Name annotation preserving the original name");
        Assert.assertTrue(merged.contains("Response1 response;"),
                "a field referencing the renamed type should be updated to the new name");
        Assert.assertTrue(merged.contains("Response1[] responses;"),
                "an array field referencing the renamed type should be updated to the new name");
    }

    @Test
    public void testMergeLeavesNonCollidingTypesUntouched() {
        SyntaxTree first = syntaxTreeFrom("""
                public type Context record {|
                    string result;
                |};
                """);
        SyntaxTree second = syntaxTreeFrom("""
                public type PublishResult record {|
                    string status;
                |};
                """);

        String merged = CodeGenerator.merge(first, second).toSourceCode();

        Assert.assertTrue(merged.contains("public type Context record"));
        Assert.assertTrue(merged.contains("public type PublishResult record"));
        Assert.assertFalse(merged.contains("xmldata:Name"),
                "no rename should have happened, so no @xmldata:Name annotation should be introduced");
    }

    private static SyntaxTree syntaxTreeFrom(String source) {
        ModulePartNode modulePartNode = NodeParser.parseModulePart(source);
        return SyntaxTree.from(TextDocuments.from("")).modifyWith(modulePartNode);
    }
}
