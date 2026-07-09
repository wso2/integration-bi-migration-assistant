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
import synapse.model.Synapse.Kind;
import synapse.reader.SynapseConfigReader;

import java.io.File;
import java.nio.file.Path;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

/**
 * End-to-end tests for {@link DependencyGraph#buildDependencyGraph}: artifact-node creation, the
 * dependency adjacency map, the leaf-first ordering, cycle reporting and unresolved references, built
 * from real Synapse artifact files.
 */
public class DependencyGraphTest {

    private static final Path GRAPH = Path.of("src", "test", "resources", "dependency-graph");

    private static DependencyGraph build(Path dir) {
        List<File> files = SynapseConfigReader.collectArtifactFiles(dir.toString());
        return DependencyGraph.buildDependencyGraph(files);
    }

    private static Set<String> ids(Collection<ArtifactNode> nodes) {
        return nodes.stream().map(ArtifactNode::id).collect(Collectors.toSet());
    }

    private static ArtifactNode nodeById(DependencyGraph graph, String id) {
        return graph.nodes().keySet().stream()
                .filter(node -> node.id().equals(id)).findFirst().orElseThrow();
    }

    private static List<String> sortedIds(DependencyGraph graph) {
        return graph.sortedNodes().stream().map(ArtifactNode::id).collect(Collectors.toList());
    }

    @Test
    public void buildsArtifactNodesWithIdNameKindAndFile() {
        DependencyGraph graph = build(GRAPH.resolve("linear-chain"));
        assertEquals(ids(graph.nodes().keySet()),
                Set.of("API:HelloWorld", "SEQUENCE:foo", "SEQUENCE:bar", "SEQUENCE:baz"));
        ArtifactNode api = nodeById(graph, "API:HelloWorld");
        assertEquals(api.name(), "HelloWorld");
        assertEquals(api.kind(), Kind.API);
        assertNotNull(api.file());
        assertTrue(api.file().toString().endsWith(".xml"));
    }

    @Test
    public void buildsAdjacencyEdges() {
        DependencyGraph graph = build(GRAPH.resolve("linear-chain"));
        assertEquals(ids(graph.nodes().get(nodeById(graph, "API:HelloWorld"))), Set.of("SEQUENCE:foo"));
        assertEquals(ids(graph.nodes().get(nodeById(graph, "SEQUENCE:foo"))), Set.of("SEQUENCE:bar"));
        assertEquals(ids(graph.nodes().get(nodeById(graph, "SEQUENCE:bar"))), Set.of("SEQUENCE:baz"));
        assertTrue(graph.nodes().get(nodeById(graph, "SEQUENCE:baz")).isEmpty());
    }

    @Test
    public void sortsLeafFirst() {
        DependencyGraph graph = build(GRAPH.resolve("linear-chain"));
        assertEquals(sortedIds(graph),
                List.of("SEQUENCE:baz", "SEQUENCE:bar", "SEQUENCE:foo", "API:HelloWorld"));
        assertTrue(graph.cycles().isEmpty());
        assertTrue(graph.unresolvedNodes().isEmpty());
    }

    @Test
    public void sharedDependencyListedOnceAndBeforeDependents() {
        DependencyGraph graph = build(GRAPH.resolve("shared-dependency"));
        List<String> sorted = sortedIds(graph);
        assertEquals(sorted.size(), 3);
        assertTrue(sorted.indexOf("SEQUENCE:foo") < sorted.indexOf("API:HelloWorld"));
        assertTrue(sorted.indexOf("SEQUENCE:foo") < sorted.indexOf("SEQUENCE:wrapper"));
        assertEquals(ids(graph.nodes().get(nodeById(graph, "API:HelloWorld"))), Set.of("SEQUENCE:foo"));
        assertEquals(ids(graph.nodes().get(nodeById(graph, "SEQUENCE:wrapper"))), Set.of("SEQUENCE:foo"));
    }

    @Test
    public void nodeWithMultipleIndependentDependencies() {
        DependencyGraph graph = build(GRAPH.resolve("multiple-dependencies"));
        assertEquals(ids(graph.nodes().keySet()),
                Set.of("SEQUENCE:a", "SEQUENCE:b", "SEQUENCE:c", "SEQUENCE:d", "SEQUENCE:e", "SEQUENCE:f"));
        // a depends on b, c and d; d depends on the leaves e and f; b and c are leaves too.
        assertEquals(ids(graph.nodes().get(nodeById(graph, "SEQUENCE:a"))),
                Set.of("SEQUENCE:b", "SEQUENCE:c", "SEQUENCE:d"));
        assertEquals(ids(graph.nodes().get(nodeById(graph, "SEQUENCE:d"))),
                Set.of("SEQUENCE:e", "SEQUENCE:f"));
        assertTrue(graph.nodes().get(nodeById(graph, "SEQUENCE:b")).isEmpty());
        assertTrue(graph.nodes().get(nodeById(graph, "SEQUENCE:c")).isEmpty());
        assertTrue(graph.nodes().get(nodeById(graph, "SEQUENCE:e")).isEmpty());
        assertTrue(graph.nodes().get(nodeById(graph, "SEQUENCE:f")).isEmpty());

        // Leaf-first: e and f come before d, and b, c, d all come before a, which is last.
        List<String> sorted = sortedIds(graph);
        assertEquals(sorted.size(), 6);
        assertEquals(sorted.get(5), "SEQUENCE:a");
        assertTrue(sorted.indexOf("SEQUENCE:e") < sorted.indexOf("SEQUENCE:d"));
        assertTrue(sorted.indexOf("SEQUENCE:f") < sorted.indexOf("SEQUENCE:d"));
        assertTrue(sorted.indexOf("SEQUENCE:b") < sorted.indexOf("SEQUENCE:a"));
        assertTrue(sorted.indexOf("SEQUENCE:c") < sorted.indexOf("SEQUENCE:a"));
        assertTrue(sorted.indexOf("SEQUENCE:d") < sorted.indexOf("SEQUENCE:a"));
        assertTrue(graph.cycles().isEmpty());
        assertTrue(graph.unresolvedNodes().isEmpty());
    }

    @Test
    public void detectsTwoNodeCycle() {
        DependencyGraph graph = build(GRAPH.resolve("cyclic"));
        assertEquals(graph.cycles().size(), 1);
        assertEquals(ids(graph.cycles().get(0)), Set.of("SEQUENCE:a", "SEQUENCE:b"));
        assertEquals(graph.sortedNodes().size(), 2);
    }

    @Test
    public void detectsSelfLoop() {
        DependencyGraph graph = build(GRAPH.resolve("self-loop"));
        assertEquals(graph.cycles().size(), 1);
        assertEquals(ids(graph.cycles().get(0)), Set.of("SEQUENCE:s"));
    }

    @Test
    public void collectsUnresolvedReferences() {
        DependencyGraph graph = build(GRAPH.resolve("unresolved"));
        assertEquals(ids(graph.unresolvedNodes()), Set.of("SEQUENCE:ghost"));
        assertFalse(ids(graph.nodes().keySet()).contains("SEQUENCE:ghost"));
        for (List<ArtifactNode> dependencies : graph.nodes().values()) {
            assertFalse(ids(dependencies).contains("SEQUENCE:ghost"));
        }
    }

    @Test
    public void proxyOnlyProjectHasNoArtifacts() {
        DependencyGraph graph = build(GRAPH.resolve("proxy-only"));
        assertTrue(graph.nodes().isEmpty());
        assertTrue(graph.sortedNodes().isEmpty());
        assertTrue(graph.cycles().isEmpty());
        assertTrue(graph.unresolvedNodes().isEmpty());
    }
}
