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

/**
 * Utility class for matching paths with flexible path resolution.
 * Handles cases where paths may or may not start with '/' and supports
 * matching when one path is a suffix of another.
 */
public final class PathMatcher {

    private PathMatcher() {
        // Utility class
    }

    /**
     * Matches two paths with flexible path resolution.
     * 
     * @param resourcePath the full resource path
     * @param lookupPath the path being looked up
     * @return true if the paths match, false otherwise
     */
    public static boolean matches(String resourcePath, String lookupPath) {
        assert resourcePath != null : "resourcePath cannot be null";
        assert lookupPath != null : "lookupPath cannot be null";

        // Normalize both paths by removing leading '/'
        String normalizedResourcePath = normalizePath(resourcePath);
        String normalizedLookupPath = normalizePath(lookupPath);

        // Check if resource path ends with lookup path
        return normalizedResourcePath.endsWith(normalizedLookupPath);
    }

    /**
     * Normalizes a path by removing leading slash if present.
     * 
     * @param path the path to normalize
     * @return the normalized path
     */
    private static String normalizePath(String path) {
        return path.startsWith("/") ? path.substring(1) : path;
    }
}

