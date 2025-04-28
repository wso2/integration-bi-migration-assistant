/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package converter.tibco.analyzer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

class Graph<E> {

    final Map<E, List<E>> children = new HashMap<>();
    final List<E> roots = new ArrayList<>();

    void addEdge(E parent, E child) {
        assert parent != null && child != null;
        children.computeIfAbsent(parent, k -> new ArrayList<>()).add(child);
    }

    void addRoot(E root) {
        assert root != null;
        if (roots.contains(root)) {
            return;
        }
        roots.add(root);
    }

    List<E> topologicalSort() {
        return topologicalSortWithRoots(roots);
    }

    List<E> topologicalSortWithRoots(Collection<E> roots) {
        List<E> result = new ArrayList<>();
        Set<E> visited = new HashSet<>();
        Set<E> temp = new HashSet<>();

        for (E root : roots) {
            if (!visited.contains(root)) {
                if (!dfsVisit(root, visited, temp, result)) {
                    throw new IllegalStateException("Graph contains a cycle");
                }
            }
        }

        Collections.reverse(result);
        return result;
    }

    private boolean dfsVisit(E node, Set<E> visited, Set<E> temp, List<E> result) {
        if (temp.contains(node)) {
            return false; // Cycle detected
        }
        if (visited.contains(node)) {
            return true; // Already processed
        }

        temp.add(node);
        visited.add(node);

        List<E> childrenList = children.get(node);
        if (childrenList != null) {
            for (E child : childrenList) {
                if (!dfsVisit(child, visited, temp, result)) {
                    return false;
                }
            }
        }

        temp.remove(node);
        result.add(node);
        return true;
    }
}
