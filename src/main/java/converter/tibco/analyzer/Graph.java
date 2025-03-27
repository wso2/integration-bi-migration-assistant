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

package converter.tibco.analyzer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

class Graph<E> {

    final Map<E, List<E>> children = new HashMap<>();
    final List<E> roots = new ArrayList<>();

    void addEdge(E parent, E child) {
        assert parent != null && child != null;
        children.computeIfAbsent(parent, k -> new ArrayList<>()).add(child);
    }

    void addRoot(E root) {
        assert root != null;
        roots.add(root);
    }

    List<E> topologicalSort() {
        // Create a map to track in-degrees for each node
        Map<E, Integer> inDegree = new HashMap<>();

        // Initialize in-degrees for all nodes
        for (E node : children.keySet()) {
            inDegree.put(node, 0);
        }

        // Calculate in-degrees for all nodes
        for (List<E> edges : children.values()) {
            for (E node : edges) {
                inDegree.merge(node, 1, Integer::sum);
            }
        }

        // Initialize result list and queue
        List<E> result = new ArrayList<>();
        List<E> queue = new ArrayList<>(roots);

        // Process nodes with zero in-degree
        while (!queue.isEmpty()) {
            E node = queue.remove(0);
            result.add(node);

            // Reduce in-degree for all children and add to queue if in-degree becomes 0
            List<E> childrenList = children.get(node);
            if (childrenList != null) {
                for (E child : childrenList) {
                    inDegree.merge(child, -1, Integer::sum);
                    if (inDegree.get(child) == 0) {
                        queue.add(child);
                    }
                }
            }
        }

        return result;
    }
}
