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

package tibco.util;

import org.testng.annotations.Test;

import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertTrue;

class PathMatcherTest {

    @Test
    void testExactMatch() {
        assertTrue(PathMatcher.matches("path/to/resource", "path/to/resource"));
        assertTrue(PathMatcher.matches("/path/to/resource", "/path/to/resource"));
    }

    @Test
    void testMatchWithLeadingSlashDifferences() {
        assertTrue(PathMatcher.matches("/path/to/resource", "path/to/resource"));
        assertTrue(PathMatcher.matches("path/to/resource", "/path/to/resource"));
    }

    @Test
    void testSuffixMatch() {
        assertTrue(PathMatcher.matches("full/path/to/resource", "to/resource"));
        assertTrue(PathMatcher.matches("full/path/to/resource", "resource"));
        assertTrue(PathMatcher.matches("/full/path/to/resource", "/to/resource"));
        assertTrue(PathMatcher.matches("/full/path/to/resource", "to/resource"));
    }

    @Test
    void testNoMatch() {
        assertFalse(PathMatcher.matches("path/to/resource", "different/path"));
        assertFalse(PathMatcher.matches("path/to/resource", "path/to/different"));
        assertFalse(PathMatcher.matches("short", "much/longer/path"));
    }

    @Test
    void testEmptyPaths() {
        assertTrue(PathMatcher.matches("", ""));
        assertFalse(PathMatcher.matches("path", ""));
        assertTrue(PathMatcher.matches("path", "path"));
    }

    @Test
    void testSingleSegmentPaths() {
        assertTrue(PathMatcher.matches("resource", "resource"));
        assertTrue(PathMatcher.matches("/resource", "resource"));
        assertTrue(PathMatcher.matches("resource", "/resource"));
        assertTrue(PathMatcher.matches("/resource", "/resource"));
    }

    @Test
    void testPartialMatch() {
        assertFalse(PathMatcher.matches("path/to/res", "resource"));
        assertFalse(PathMatcher.matches("path/resource", "path/res"));
    }
}
