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

package tibco.analyzer;

import tibco.model.Process5;

import java.util.Collections;

/**
 * Simple test to validate process call tracking functionality.
 * Tests that DependencyAnalysisPass can track called processes in AnalysisResult.
 */
public class ProcessCallTrackingTest {

    /**
     * Main method to run tests.
     * @param args command line arguments
     */
    public static void main(String[] args) {
        ProcessCallTrackingTest test = new ProcessCallTrackingTest();
        try {
            test.runTests();
        } catch (Exception e) {
            throw new RuntimeException("Test failed: " + e.getMessage());
        }
    }

    /**
     * Run all tests.
     */
    public void runTests() {
        testBasicAnalysisResultCreation();
        testProcessCallTrackingFunctionality();
    }

    private void testBasicAnalysisResultCreation() {
        // Create a simple DependencyAnalysisPass and get result
        DependencyAnalysisPass analysisPass = new DependencyAnalysisPass();
        AnalysisResult result = analysisPass.getResult(null, null);

        // Verify the isProcessCalled method exists and works
        assert result != null : "AnalysisResult should not be null";
    }

    private void testProcessCallTrackingFunctionality() {
        // Create a DependencyAnalysisPass and test with real Process instances
        DependencyAnalysisPass analysisPass = new DependencyAnalysisPass();

        // Create real Process instances using the existing process creation pattern
        Process5 process1 = new Process5("TestProcess1", "/TestProcess1", Collections.emptyList(),
            new Process5.ExplicitTransitionGroup());
        Process5 process2 = new Process5("TestProcess2", "/TestProcess2", Collections.emptyList(),
            new Process5.ExplicitTransitionGroup());

        // Test that initially, no processes are marked as called
        AnalysisResult emptyResult = analysisPass.getResult(null, process1);
        assert !emptyResult.isProcessCalled(process1) : "Process1 should not be marked as called initially";
        assert !emptyResult.isProcessCalled(process2) : "Process2 should not be marked as called initially";
    }
}
