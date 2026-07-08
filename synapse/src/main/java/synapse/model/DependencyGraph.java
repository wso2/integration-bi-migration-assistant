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
 *  KIND, either express or implied. See the License for the
 *  specific language governing permissions and limitations
 *  under the License.
 */
package synapse.model;

import synapse.model.Synapse.Api;
import synapse.model.Synapse.Kind;
import synapse.model.Synapse.Sequence;
import synapse.model.Synapse.SynapseNode;
import synapse.reader.SynapseConfigReader;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class DependencyGraph {

    private final Map<ArtifactNode, List<ArtifactNode>> nodes;
    private final List<ArtifactNode> sortedNodes;
    private final List<List<ArtifactNode>> cycles;
    private final List<ArtifactNode> unresolvedNodes;

    public DependencyGraph(Map<ArtifactNode, List<ArtifactNode>> nodes, List<ArtifactNode> sortedNodes,
                           List<List<ArtifactNode>> cycles, List<ArtifactNode> unresolvedNodes) {
        this.nodes = nodes;
        this.sortedNodes = sortedNodes;
        this.cycles = cycles;
        this.unresolvedNodes = unresolvedNodes;
    }

    public static DependencyGraph buildDependencyGraph(List<File> artifactFiles) {
        List<ArtifactNode> artifactNodes = collectArtifactNodes(artifactFiles);
        DependencyResolver resolver = new DependencyResolver(artifactNodes);

        Map<ArtifactNode, List<ArtifactNode>> nodes = new LinkedHashMap<>();
        for (ArtifactNode node : artifactNodes) {
            nodes.put(node, resolver.resolve(node));
        }
        TopologicalSorter sorter = new TopologicalSorter(nodes);
        sorter.sort();
        return new DependencyGraph(nodes, sorter.sorted(), sorter.cycles(),
                new ArrayList<>(resolver.unresolvedNodes()));
    }

    private static List<ArtifactNode> collectArtifactNodes(List<File> artifactFiles) {
        List<ArtifactNode> nodes = new ArrayList<>();
        for (File artifact : artifactFiles) {
            Path file = artifact.toPath();
            for (SynapseNode node : SynapseConfigReader.parse(artifact)) {
                artifactNode(node, file).ifPresent(nodes::add);
            }
        }
        return nodes;
    }

    private static Optional<ArtifactNode> artifactNode(SynapseNode node, Path file) {
        return switch (node) {
            case Api api -> Optional.of(new ArtifactNode(api.kind() + ":" + api.name(), api.name(),
                    api.kind(), file));
            case Sequence sequence -> Optional.of(new ArtifactNode(sequence.kind() + ":" + sequence.name(),
                    sequence.name(), sequence.kind(), file));
            default -> Optional.empty();
        };
    }

    public Map<ArtifactNode, List<ArtifactNode>> nodes() {
        return nodes;
    }

    public List<ArtifactNode> sortedNodes() {
        return sortedNodes;
    }

    public List<List<ArtifactNode>> cycles() {
        return cycles;
    }

    public List<ArtifactNode> unresolvedNodes() {
        return unresolvedNodes;
    }

    public record ArtifactNode(String id, String name, Kind kind, Path file) {
    }

    /**
     * Orders the artifacts of a dependency graph leaf-first and reports dependency cycles.
     *
     * <p>Runs a depth-first search where an edge {@code A -> B} means "A depends on B". An artifact is
     * emitted only once all of its dependencies have been emitted (post-order), so dependencies
     * (leaves) come first. A back edge to an artifact still on the current DFS path marks a cycle,
     * recorded as the chain of artifacts from that artifact down to the one that closes the loop.
     */
    public static class TopologicalSorter {

        private enum Mark { VISITING, VISITED }

        private final Map<ArtifactNode, List<ArtifactNode>> dependencies;

        private final Map<ArtifactNode, Mark> marks = new HashMap<>();
        private final List<ArtifactNode> path = new ArrayList<>();
        private final List<ArtifactNode> sorted = new ArrayList<>();
        private final List<List<ArtifactNode>> cycles = new ArrayList<>();

        public TopologicalSorter(Map<ArtifactNode, List<ArtifactNode>> dependencies) {
            this.dependencies = dependencies;
        }

        public void sort() {
            for (ArtifactNode node : dependencies.keySet()) {
                if (!marks.containsKey(node)) {
                    visit(node);
                }
            }
        }

        public List<ArtifactNode> sorted() {
            return sorted;
        }

        public List<List<ArtifactNode>> cycles() {
            return cycles;
        }

        private void visit(ArtifactNode node) {
            marks.put(node, Mark.VISITING);
            path.add(node);

            for (ArtifactNode dependency : dependencies.getOrDefault(node, List.of())) {
                Mark mark = marks.get(dependency);
                if (mark == null) {
                    visit(dependency);
                } else if (mark == Mark.VISITING) {
                    cycles.add(new ArrayList<>(path.subList(path.indexOf(dependency), path.size())));
                }
            }

            path.remove(path.size() - 1);
            marks.put(node, Mark.VISITED);
            sorted.add(node);
        }
    }
}
