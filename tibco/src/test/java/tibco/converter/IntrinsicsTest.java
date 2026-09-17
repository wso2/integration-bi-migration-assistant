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

package tibco.converter;

import org.testng.Assert;
import org.testng.annotations.Test;

public class IntrinsicsTest {

    @Test(groups = { "tibco", "converter" })
    public void testPsgLogLevelToSeverity() {
        String body = Intrinsics.PSG_LOG.body;
        assertLevelMapsToSeverity(body, "Warning", "log:printWarn");
        assertLevelMapsToSeverity(body, "Error", "log:printError");
        assertLevelMapsToSeverity(body, "Debug", "log:printDebug");
    }

    private static void assertLevelMapsToSeverity(String body, String level, String logFunction) {
        String levelArm = "\"%s\" =>".formatted(level);
        int levelIndex = body.indexOf(levelArm);
        Assert.assertTrue(levelIndex >= 0, "Expected a match arm for Level \"%s\"".formatted(level));
        int armStart = levelIndex + levelArm.length();
        int nextArmIndex = body.indexOf("=>", armStart);
        String armBody = body.substring(armStart, nextArmIndex == -1 ? body.length() : nextArmIndex);
        Assert.assertTrue(armBody.contains(logFunction),
                "Expected Level \"%s\" to call %s".formatted(level, logFunction));
    }

    @Test(groups = { "tibco", "converter" })
    public void testPsgExceptionLogLogsError() {
        String body = Intrinsics.PSG_EXCEPTION_LOG.body;
        Assert.assertTrue(body.contains("error psgError = error(errorMessage"),
                "Expected a synthesized error built from the extracted fault fields");
        Assert.assertTrue(body.contains("log:printError(errorMessage, 'error = psgError"),
                "Expected the synthesized error to be logged via log:printError");
        Assert.assertTrue(body.contains("tibcoStackTrace = stackTrace"),
                "Expected the TIBCO stack trace text to be logged under a non-colliding key name");
        int logCallIndex = body.indexOf("log:printError(errorMessage");
        Assert.assertTrue(logCallIndex >= 0, "Expected a log:printError(errorMessage, ...) call");
        String logCall = body.substring(logCallIndex);
        Assert.assertFalse(logCall.contains("stackTrace = stackTrace"),
                "stackTrace is a reserved log:printError parameter (error:StackFrame[]?); "
                        + "passing a string there is a type mismatch");
    }
}
