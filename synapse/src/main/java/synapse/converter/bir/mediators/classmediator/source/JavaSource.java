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
package synapse.converter.bir.mediators.classmediator.source;

/**
 * The Java source located for a class mediator, together with where it came from. {@link Origin}
 * lets callers distinguish genuine source from decompiled bytecode (lower fidelity — recovered
 * local names, no comments), which is worth surfacing to the developer.
 *
 * @param className fully-qualified class name that was resolved
 * @param source    the Java source text
 * @param origin    where the source was obtained from
 */
public record JavaSource(String className, String source, Origin origin) {

    public enum Origin {
        /** A loose {@code .java} file under a configured source root (a dev project). */
        SOURCE_FILE,
        /** A {@code .java} entry inside a jar (e.g. a shipped {@code *-sources.jar}). */
        SOURCES_JAR,
        /** Reconstructed from a {@code .class} entry by a {@link Decompiler}. */
        DECOMPILED
    }
}
