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
package synapse.model;

import org.testng.annotations.Test;
import synapse.model.DependencyGraph.ArtifactNode;
import synapse.model.DependencyGraph.TopologicalSorter;
import synapse.model.Synapse.Kind;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for {@link TopologicalSorter}, exercised directly on hand-built dependency maps so the
 * leaf-first ordering and cycle detection are tested in isolation from parsing and graph building.
 */
public class TopologicalSorterTest {

    private static ArtifactNode node(String name) {
        return new ArtifactNode(Kind.SEQUENCE + ":" + name, name, Kind.SEQUENCE, null);
    }

    private static Set<String> ids(List<ArtifactNode> nodes) {
        return nodes.stream().map(ArtifactNode::id).collect(Collectors.toSet());
    }

    private static TopologicalSorter sort(Map<ArtifactNode, List<ArtifactNode>> graph) {
        TopologicalSorter sorter = new TopologicalSorter(graph);
        sorter.sort();
        return sorter;
    }

    private static void assertDependenciesFirst(Map<ArtifactNode, List<ArtifactNode>> graph,
                                                List<ArtifactNode> sorted) {
        assertEquals(Set.copyOf(sorted), graph.keySet(), "every node listed exactly once");
        assertEquals(sorted.size(), graph.size(), "no duplicates in the sorted output");
        for (Map.Entry<ArtifactNode, List<ArtifactNode>> entry : graph.entrySet()) {
            int dependentIndex = sorted.indexOf(entry.getKey());
            for (ArtifactNode dependency : entry.getValue()) {
                assertTrue(sorted.indexOf(dependency) < dependentIndex,
                        dependency.id() + " must come before " + entry.getKey().id());
            }
        }
    }

    @Test
    public void emptyGraph() {
        TopologicalSorter sorter = sort(Map.of());
        assertTrue(sorter.sorted().isEmpty());
        assertTrue(sorter.cycles().isEmpty());
    }

    @Test
    public void singleNodeNoDependencies() {
        ArtifactNode a = node("a");
        TopologicalSorter sorter = sort(Map.of(a, List.of()));
        assertEquals(sorter.sorted(), List.of(a));
        assertTrue(sorter.cycles().isEmpty());
    }

    @Test
    public void linearChainIsLeafFirst() {
        ArtifactNode a = node("a");
        ArtifactNode b = node("b");
        ArtifactNode c = node("c");
        TopologicalSorter sorter = sort(Map.of(a, List.of(b), b, List.of(c), c, List.of()));
        assertEquals(sorter.sorted(), List.of(c, b, a));
        assertTrue(sorter.cycles().isEmpty());
    }

    @Test
    public void diamondPlacesSharedDependencyFirst() {
        ArtifactNode a = node("a");
        ArtifactNode b = node("b");
        ArtifactNode c = node("c");
        ArtifactNode d = node("d");
        Map<ArtifactNode, List<ArtifactNode>> graph = Map.of(
                a, List.of(b, c), b, List.of(d), c, List.of(d), d, List.of());
        TopologicalSorter sorter = sort(graph);
        assertTrue(sorter.cycles().isEmpty());
        assertDependenciesFirst(graph, sorter.sorted());
    }

    @Test
    public void multipleIndependentComponents() {
        ArtifactNode a = node("a");
        ArtifactNode b = node("b");
        ArtifactNode x = node("x");
        ArtifactNode y = node("y");
        Map<ArtifactNode, List<ArtifactNode>> graph = Map.of(
                a, List.of(b), b, List.of(), x, List.of(y), y, List.of());
        TopologicalSorter sorter = sort(graph);
        assertTrue(sorter.cycles().isEmpty());
        assertDependenciesFirst(graph, sorter.sorted());
    }

    @Test
    public void twoNodeCycleIsDetected() {
        ArtifactNode a = node("a");
        ArtifactNode b = node("b");
        TopologicalSorter sorter = sort(Map.of(a, List.of(b), b, List.of(a)));
        assertEquals(sorter.sorted().size(), 2, "both nodes still listed");
        assertEquals(sorter.cycles().size(), 1);
        assertEquals(ids(sorter.cycles().get(0)), Set.of("SEQUENCE:a", "SEQUENCE:b"));
    }

    @Test
    public void selfLoopIsDetected() {
        ArtifactNode a = node("a");
        TopologicalSorter sorter = sort(Map.of(a, List.of(a)));
        assertEquals(sorter.cycles().size(), 1);
        assertEquals(sorter.cycles().get(0), List.of(a));
    }

    @Test
    public void longerCycleIsDetected() {
        ArtifactNode a = node("a");
        ArtifactNode b = node("b");
        ArtifactNode c = node("c");
        TopologicalSorter sorter = sort(Map.of(a, List.of(b), b, List.of(c), c, List.of(a)));
        assertEquals(sorter.cycles().size(), 1);
        assertEquals(ids(sorter.cycles().get(0)), Set.of("SEQUENCE:a", "SEQUENCE:b", "SEQUENCE:c"));
    }

    @Test
    public void cycleWithAcyclicTailStillListsAllNodes() {
        ArtifactNode a = node("a");
        ArtifactNode b = node("b");
        ArtifactNode c = node("c");
        ArtifactNode d = node("d");
        Map<ArtifactNode, List<ArtifactNode>> graph = new HashMap<>();
        graph.put(a, List.of(b));
        graph.put(b, List.of(a));
        graph.put(c, List.of(a));
        graph.put(d, List.of(c));
        TopologicalSorter sorter = sort(graph);
        assertEquals(sorter.sorted().size(), 4);
        assertTrue(sorter.cycles().stream()
                .anyMatch(cycle -> ids(cycle).equals(Set.of("SEQUENCE:a", "SEQUENCE:b"))));
    }

    @Test
    public void toleratesDependencyMissingFromKeySet() {
        ArtifactNode a = node("a");
        ArtifactNode b = node("b");
        TopologicalSorter sorter = sort(Map.of(a, List.of(b)));
        assertTrue(sorter.sorted().contains(a));
        assertTrue(sorter.cycles().isEmpty());
    }
}
