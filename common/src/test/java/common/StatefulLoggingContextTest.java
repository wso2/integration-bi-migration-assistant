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

import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

class StatefulLoggingContextTest {

    private List<String> stateOutput;
    private List<String> logOutput;
    private StatefulLoggingContext context;

    @BeforeMethod
    void setUp() {
        stateOutput = new ArrayList<>();
        logOutput = new ArrayList<>();
        context = new StatefulLoggingContext(
            stateOutput::add,
            logOutput::add
        );
    }

    @Test
    void testLogWithoutStateThrowsException() {
        try {
            context.log(LoggingUtils.Level.INFO, "test message");
            Assert.fail("Expected IllegalStateException");
        } catch (IllegalStateException exception) {
            Assert.assertEquals(exception.getMessage(), 
                               "Cannot log without first setting a state. Call logState() first.");
        }
    }

    @Test
    void testBasicStateAndLogging() {
        // Log first state
        context.logState("Initial State");
        
        // Verify state output
        Assert.assertEquals(stateOutput.size(), 1);
        Assert.assertEquals(logOutput.size(), 1);
        Assert.assertEquals(logOutput.get(0), ""); // Empty line for spacing
        Assert.assertEquals(stateOutput.get(0), "\u001B[1m▶ Initial State\u001B[0m");
        
        // Clear outputs for next assertions
        stateOutput.clear();
        logOutput.clear();
        
        // Log a message
        context.log(LoggingUtils.Level.INFO, "Test message");
        
        // Verify log output with formatting
        Assert.assertEquals(logOutput.size(), 1);
        Assert.assertEquals(logOutput.get(0), "  \u001B[2mℹ️  Test message\u001B[0m");
    }

    @Test
    void testStateTransition() {
        // First state with log
        context.logState("State 1");
        context.log(LoggingUtils.Level.INFO, "Message 1");
        
        // Clear to focus on transition
        stateOutput.clear();
        logOutput.clear();
        
        // Second state - should complete first state
        context.logState("State 2");
        
        // Verify completion sequence
        Assert.assertTrue(stateOutput.size() >= 2);
        
        // Should contain ANSI clear commands
        String clearCommands = stateOutput.get(0);
        Assert.assertTrue(clearCommands.contains("\u001B[1A")); // Move up
        Assert.assertTrue(clearCommands.contains("\u001B[2K")); // Clear line
        
        // Should rewrite completed state with ◉
        String completedState = stateOutput.get(1);
        Assert.assertEquals(completedState, "\u001B[1m◉ State 1\u001B[0m");
        
        // Should replay the log message
        Assert.assertEquals(logOutput.get(0), "  \u001B[2mℹ️  Message 1\u001B[0m");
        Assert.assertEquals(logOutput.get(1), ""); // Empty line after completed state
        
        // Should start new state
        Assert.assertEquals(logOutput.get(2), ""); // Empty line for spacing
        String newState = stateOutput.get(2);
        Assert.assertEquals(newState, "\u001B[1m▶ State 2\u001B[0m");
    }

    @Test
    void testMarkCurrentStateComplete() {
        // Set up state with logs
        context.logState("Final State");
        context.log(LoggingUtils.Level.INFO, "Final message");
        
        // Clear to focus on completion
        stateOutput.clear();
        logOutput.clear();
        
        // Mark complete
        context.markCurrentStateComplete();
        
        // Verify completion sequence
        Assert.assertTrue(stateOutput.size() >= 2);
        
        // Should contain ANSI clear commands
        String clearCommands = stateOutput.get(0);
        Assert.assertTrue(clearCommands.contains("\u001B[1A"));
        Assert.assertTrue(clearCommands.contains("\u001B[2K"));
        
        // Should rewrite completed state
        Assert.assertEquals(stateOutput.get(1), "\u001B[1m◉ Final State\u001B[0m");
        
        // Should replay log messages
        Assert.assertEquals(logOutput.get(0), "  \u001B[2mℹ️  Final message\u001B[0m");
        Assert.assertEquals(logOutput.get(1), ""); // Empty line
    }

    @Test
    void testLogLevelFormatting() {
        context.logState("Test State");
        
        // Clear state setup output
        logOutput.clear();
        
        // Test different log levels
        context.log(LoggingUtils.Level.ERROR, "Error message");
        context.log(LoggingUtils.Level.SEVERE, "Severe message");
        context.log(LoggingUtils.Level.WARN, "Warning message");
        context.log(LoggingUtils.Level.INFO, "Info message");
        context.log(LoggingUtils.Level.DEBUG, "Debug message");
        
        // Verify formatting
        Assert.assertEquals(logOutput.get(0), "  \u001B[31m❌ Error message\u001B[0m");
        Assert.assertEquals(logOutput.get(1), "  \u001B[31m🚨 Severe message\u001B[0m");
        Assert.assertEquals(logOutput.get(2), "  ⚠️  Warning message");
        Assert.assertEquals(logOutput.get(3), "  \u001B[2mℹ️  Info message\u001B[0m");
        Assert.assertEquals(logOutput.get(4), "  \u001B[2m🐛 Debug message\u001B[0m");
    }

    @Test
    void testMultipleLogMessages() {
        context.logState("State with Multiple Logs");
        context.log(LoggingUtils.Level.INFO, "Message 1");
        context.log(LoggingUtils.Level.WARN, "Message 2");
        context.log(LoggingUtils.Level.ERROR, "Message 3");
        
        // Clear setup output
        stateOutput.clear();
        logOutput.clear();
        
        // Transition to new state
        context.logState("New State");
        
        // Verify all messages are replayed
        Assert.assertEquals(logOutput.get(0), "  \u001B[2mℹ️  Message 1\u001B[0m");
        Assert.assertEquals(logOutput.get(1), "  ⚠️  Message 2");
        Assert.assertEquals(logOutput.get(2), "  \u001B[31m❌ Message 3\u001B[0m");
    }

    @Test
    void testMarkCompleteWithoutActiveState() {
        // Should not throw exception when no active state
        context.markCurrentStateComplete();
        
        // Should produce no output
        Assert.assertTrue(stateOutput.isEmpty());
        Assert.assertTrue(logOutput.isEmpty());
    }

    @Test
    void testStateSequence() {
        // Test a complete sequence: State1 -> logs -> State2 -> logs -> complete
        context.logState("State 1");
        context.log(LoggingUtils.Level.INFO, "Log 1");
        
        context.logState("State 2");
        context.log(LoggingUtils.Level.WARN, "Log 2");
        
        context.markCurrentStateComplete();
        
        // Verify the sequence contains expected elements
        Assert.assertTrue(stateOutput.stream().anyMatch(s -> s.contains("▶ State 1")));
        Assert.assertTrue(stateOutput.stream().anyMatch(s -> s.contains("◉ State 1")));
        Assert.assertTrue(stateOutput.stream().anyMatch(s -> s.contains("▶ State 2")));
        Assert.assertTrue(stateOutput.stream().anyMatch(s -> s.contains("◉ State 2")));
        
        Assert.assertTrue(logOutput.stream().anyMatch(s -> s.contains("Log 1")));
        Assert.assertTrue(logOutput.stream().anyMatch(s -> s.contains("Log 2")));
    }
}
