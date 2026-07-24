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

import java.util.Optional;

/**
 * Reconstructs Java source from a compiled {@code .class} file. Used only as a last resort by
 * {@link JavaSourceResolver} when a class mediator ships as bytecode with no source.
 *
 * <p>Decompilation is best-effort: an implementation returns {@link Optional#empty()} rather than
 * throwing when it cannot produce usable source, so resolution degrades to an empty stub.
 */
public interface Decompiler {

    /**
     * @param className  fully-qualified class name
     * @param classBytes the raw bytecode of the class
     * @return reconstructed Java source, or empty if decompilation is unavailable or failed
     */
    Optional<String> decompile(String className, byte[] classBytes);

    /** A decompiler that never produces source — the default when none is configured. */
    Decompiler NONE = (className, classBytes) -> Optional.empty();
}
