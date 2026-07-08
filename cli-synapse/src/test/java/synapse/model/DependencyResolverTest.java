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

import java.nio.file.Path;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertNull;
import static org.testng.Assert.assertTrue;

/**
 * Unit tests for {@link DependencyResolver}: which artifacts a given artifact depends on (only
 * sequences), how the mediator tree is walked, and how references to unknown sequences are reported.
 */
public class DependencyResolverTest {

    private static final Path GRAPH = Path.of("src", "test", "resources", "dependency-graph");

    private static ArtifactNode sequence(String name, Path file) {
        return new ArtifactNode(Kind.SEQUENCE + ":" + name, name, Kind.SEQUENCE, file);
    }

    private static ArtifactNode api(String name, Path file) {
        return new ArtifactNode(Kind.API + ":" + name, name, Kind.API, file);
    }

    private static Set<String> ids(List<ArtifactNode> nodes) {
        return nodes.stream().map(ArtifactNode::id).collect(Collectors.toSet());
    }

    @Test
    public void resolvesApiAndSequenceReferences() {
        Path dir = GRAPH.resolve("shared-dependency");
        ArtifactNode foo = sequence("foo", dir.resolve("foo.xml"));
        ArtifactNode hello = api("HelloWorld", dir.resolve("helloWorld.xml"));
        ArtifactNode wrapper = sequence("wrapper", dir.resolve("wrapper.xml"));
        DependencyResolver resolver = new DependencyResolver(List.of(foo, hello, wrapper));

        assertEquals(ids(resolver.resolve(hello)), Set.of("SEQUENCE:foo"));
        assertEquals(ids(resolver.resolve(wrapper)), Set.of("SEQUENCE:foo"));
        assertTrue(resolver.resolve(foo).isEmpty());
        assertTrue(resolver.unresolvedNodes().isEmpty());
    }

    @Test
    public void ignoresNonSequenceMediators() {
        Path dir = GRAPH.resolve("mixed-mediators");
        ArtifactNode foo = sequence("foo", dir.resolve("foo.xml"));
        ArtifactNode bar = sequence("bar", dir.resolve("bar.xml"));
        DependencyResolver resolver = new DependencyResolver(List.of(foo, bar));

        // foo holds a property and a payloadFactory alongside the <sequence key="bar"/> reference.
        assertEquals(ids(resolver.resolve(foo)), Set.of("SEQUENCE:bar"));
    }

    @Test
    public void unresolvedReferenceProducesSyntheticNode() {
        Path dir = GRAPH.resolve("unresolved");
        ArtifactNode caller = sequence("caller", dir.resolve("caller.xml"));
        ArtifactNode u = api("U", dir.resolve("api.xml"));
        DependencyResolver resolver = new DependencyResolver(List.of(caller, u));

        assertTrue(resolver.resolve(caller).isEmpty());
        assertTrue(resolver.resolve(u).isEmpty());
        assertEquals(resolver.unresolvedNodes().size(), 1, "the missing 'ghost' sequence is deduplicated");
        ArtifactNode ghost = resolver.unresolvedNodes().iterator().next();
        assertEquals(ghost.id(), "SEQUENCE:ghost");
        assertEquals(ghost.name(), "ghost");
        assertEquals(ghost.kind(), Kind.SEQUENCE);
        assertNull(ghost.file());
    }

    @Test
    public void resourceWithoutInSequenceHasNoDependencies() {
        ArtifactNode n = api("N", GRAPH.resolve("misc").resolve("no-insequence.xml"));
        DependencyResolver resolver = new DependencyResolver(List.of(n));

        assertTrue(resolver.resolve(n).isEmpty());
        assertTrue(resolver.unresolvedNodes().isEmpty());
    }
}
