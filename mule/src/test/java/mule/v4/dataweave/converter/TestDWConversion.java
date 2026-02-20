/*
 *  Copyright (c) 2026, WSO2 LLC. (http://www.wso2.com).
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
package mule.v4.dataweave.converter;

import common.BallerinaModel;
import mule.TestUtils;
import mule.v4.Context;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.io.PrintStream;
import java.util.ArrayList;

import static mule.v4.dataweave.converter.DWReader.getFunctionStatement;

public class TestDWConversion {

    @Test
    public void testDWConversion() {
        Context ctx = TestUtils.createMockContext();;
        DWContext dWCtx = new DWContext(ctx, new ArrayList<>());
        String script = """
                %dw 2.0
                output application/json
                ---
                {
                    "name": upper("wso2"),
                    "industry": "middle ware"
                }
                """;

        String func;
        try {
            func = getFunctionStatement(script, null, dWCtx, ctx, "result", "dwTransform");
        } catch (DWCodeGenException e) {
            throw new RuntimeException(e);
        }

        BallerinaModel.TextDocument txtDoc = new BallerinaModel.TextDocument("testDW", new ArrayList<>(),
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                ctx.currentFileCtx.balConstructs.functions, new ArrayList<>());
        PrintStream out = System.out;
        Assert.assertFalse(txtDoc.toSource().isEmpty(), "something went wrong with the conversion, output is empty");
        out.println(txtDoc.toSource());
    }
}
