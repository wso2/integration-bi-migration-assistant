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

package converter.tibco;

import ballerina.BallerinaModel;
import tibco.TibcoModel;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Stream;

public class TibcoToBallerinaModelConverter {

    public static class Context {

        private enum Library {
            HTTP("http");

            public final String value;

            Library(String value) {
                this.value = value;
            }
        }

        private final Map<String, Optional<BallerinaModel.ModuleTypeDef>> moduleTypeDefs = new HashMap<>();
        private final Set<BallerinaModel.Import> imports = new HashSet<>();

        public void addModuleTypeDef(String name, BallerinaModel.ModuleTypeDef moduleTypeDef) {
            moduleTypeDefs.put(name, Optional.of(moduleTypeDef));
        }

        public BallerinaModel.TypeDesc getTypeByName(String name) {
            // TODO: how to handle names spaces
            name = XmlToTibcoModelConverter.getTagNameWithoutNameSpace(name);
            if (moduleTypeDefs.containsKey(name)) {
                new BallerinaModel.TypeDesc.TypeReference(name);
            }

            Optional<BallerinaModel.TypeDesc.BuiltinType> builtinType = mapToBuiltinType(name);
            if (builtinType.isPresent()) {
                return builtinType.get();
            }

            Optional<BallerinaModel.TypeDesc.TypeReference> libraryType = mapToLibraryType(name);
            if (libraryType.isPresent()) {
                return libraryType.get();
            }

            if (!moduleTypeDefs.containsKey(name)) {
                moduleTypeDefs.put(name, Optional.empty());
            }
            return new BallerinaModel.TypeDesc.TypeReference(name);
        }

        private Optional<BallerinaModel.TypeDesc.BuiltinType> mapToBuiltinType(String name) {
            return switch (name) {
                case "string" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.STRING);
                case "anydata" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.ANYDATA);
                default -> Optional.empty();
            };
        }

        private Optional<BallerinaModel.TypeDesc.TypeReference> mapToLibraryType(String name) {
            return switch (name) {
                case "client4XXError" -> Optional.of(getLibraryType(Library.HTTP, "NOT_FOUND"));
                case "server5XXError" -> Optional.of(getLibraryType(Library.HTTP, "INTERNAL_SERVER_ERROR"));
                default -> Optional.empty();
            };
        }

        private BallerinaModel.TypeDesc.TypeReference getLibraryType(Library library, String typeName) {
            addLibraryImport(library);
            return new BallerinaModel.TypeDesc.TypeReference(typeName);
        }

        private void addLibraryImport(Library library) {
            imports.add(new BallerinaModel.Import("ballerina", library.value, Optional.empty()));
        }

        public BallerinaModel.TextDocument finish(BallerinaModel.TextDocument textDocument) {
            for (Map.Entry<String, Optional<BallerinaModel.ModuleTypeDef>> entry : moduleTypeDefs.entrySet()) {
                if (entry.getValue().isEmpty()) {
                    throw new IllegalStateException("Type not found: " + entry.getKey());
                }
            }
            List<BallerinaModel.Import> combinedImports = Stream.concat(
                    imports.stream(),
                    textDocument.imports().stream()
            ).toList();
            return new BallerinaModel.TextDocument(
                    textDocument.documentName(),
                    combinedImports,
                    textDocument.moduleTypeDefs(),
                    textDocument.moduleVars(),
                    textDocument.listeners(),
                    textDocument.services(),
                    textDocument.functions(),
                    textDocument.Comments());
        }
    }

    private TibcoToBallerinaModelConverter() {

    }

    static BallerinaModel.Module convertProcess(Context cx, TibcoModel.Process process) {
        List<BallerinaModel.ModuleTypeDef> moduleTypeDefs = process.types().stream()
                .map(type -> convertTypes(cx, type)).flatMap(Collection::stream).toList();
        // FIXME: this is wrong(name is not a package name)
        String name = process.name();
        BallerinaModel.TextDocument textDocument =
                cx.finish(new BallerinaModel.TextDocument(name + ".bal", List.of(), moduleTypeDefs,
                        List.of(), List.of(), List.of(), List.of(), List.of()));
        return new BallerinaModel.Module(name, List.of(textDocument));
    }

    static Collection<BallerinaModel.ModuleTypeDef> convertTypes(Context cx, TibcoModel.Type type) {
        return switch (type) {
            case TibcoModel.Type.Schema schema -> convertSchema(cx, schema);
            case TibcoModel.Type.WSDLDefinition wsdlDefinition -> convertWsdlDefinition(cx, wsdlDefinition);
        };
    }

    private static Collection<BallerinaModel.ModuleTypeDef> convertSchema(Context cx, TibcoModel.Type.Schema schema) {
        // TODO: (may be) handle namespaces
        Stream<BallerinaModel.ModuleTypeDef>
                newTypeDefinitions = schema.types().stream().map(type -> convertComplexType(cx, type));
        Stream<BallerinaModel.ModuleTypeDef> typeAliases =
                schema.elements().stream().map(element -> convertTypeAlias(cx, element));
        return Stream.concat(newTypeDefinitions, typeAliases).toList();
    }

    private static BallerinaModel.ModuleTypeDef convertTypeAlias(Context cx, TibcoModel.Type.Schema.Element element) {
        // FIXME: handle namespaces
        String name = XmlToTibcoModelConverter.getTagNameWithoutNameSpace(element.name());
        BallerinaModel.TypeDesc ref = cx.getTypeByName(element.type().name());
        BallerinaModel.ModuleTypeDef defn = new BallerinaModel.ModuleTypeDef(name, ref);
        cx.addModuleTypeDef(name, defn);
        return defn;
    }

    static Collection<BallerinaModel.ModuleTypeDef> convertWsdlDefinition(Context cx,
                                                                          TibcoModel.Type.WSDLDefinition wsdlDefinition) {
        // FIXME:
        return List.of();
    }

    static BallerinaModel.ModuleTypeDef convertComplexType(Context cx, TibcoModel.Type.Schema.ComplexType complexType) {
        BallerinaModel.TypeDesc typeDesc = switch (complexType.body()) {
            case TibcoModel.Type.Schema.ComplexType.Choice choice -> convertTypeChoice(cx, choice);
            case TibcoModel.Type.Schema.ComplexType.SequenceBody sequenceBody -> convertSequenceBody(cx, sequenceBody);
            // FIXME: handle type inclusion
            case TibcoModel.Type.Schema.ComplexType.ComplexContent complexContent ->
                    convertTypeInclusion(cx, complexContent);
        };
        String name = complexType.name();
        BallerinaModel.ModuleTypeDef typeDef = new BallerinaModel.ModuleTypeDef(name, typeDesc);
        cx.addModuleTypeDef(name, typeDef);
        return typeDef;
    }

    private static BallerinaModel.TypeDesc.RecordTypeDesc convertTypeInclusion(Context cx,
                                                                               TibcoModel.Type.Schema.ComplexType.ComplexContent complexContent) {
        List<BallerinaModel.TypeDesc> inclusions = List.of(cx.getTypeByName(complexContent.extension().base().name()));
        RecordBody body = getRecordBody(cx, complexContent.extension().elements());
        return new BallerinaModel.TypeDesc.RecordTypeDesc(inclusions, body.fields(), body.rest());
    }

    private static BallerinaModel.TypeDesc.RecordTypeDesc convertSequenceBody(Context cx,
                                                                              TibcoModel.Type.Schema.ComplexType.SequenceBody sequenceBody) {
        Collection<TibcoModel.Type.Schema.ComplexType.SequenceBody.Member> members = sequenceBody.elements();
        RecordBody body = getRecordBody(cx, members);
        return new BallerinaModel.TypeDesc.RecordTypeDesc(List.of(), body.fields(), body.rest());
    }

    private static <E extends TibcoModel.Type.Schema.ComplexType.SequenceBody.Member> RecordBody getRecordBody(
            Context cx, Collection<E> members) {
        List<BallerinaModel.TypeDesc.RecordTypeDesc.RecordField> fields = new ArrayList<>();
        Optional<BallerinaModel.TypeDesc> rest = Optional.empty();
        for (TibcoModel.Type.Schema.ComplexType.SequenceBody.Member member : members) {
            switch (member) {
                case TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.Element element -> {
                    BallerinaModel.TypeDesc typeDesc = cx.getTypeByName(element.type().name());
                    fields.add(new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField(element.name(), typeDesc));
                }
                case TibcoModel.Type.Schema.ComplexType.SequenceBody.Member.Rest ignored -> {
                    // FIXME: handle this properly
                    rest = Optional.of(PredefinedTypes.ANYDATA);
                }
            }
        }
        return new RecordBody(fields, rest);
    }

    private record RecordBody(List<BallerinaModel.TypeDesc.RecordTypeDesc.RecordField> fields,
                              BallerinaModel.TypeDesc rest) {

        public RecordBody(List<BallerinaModel.TypeDesc.RecordTypeDesc.RecordField> fields,
                          Optional<BallerinaModel.TypeDesc> rest) {
            this(fields, rest.orElse(BallerinaModel.TypeDesc.BuiltinType.NEVER));
        }
    }

    static BallerinaModel.TypeDesc.UnionTypeDesc convertTypeChoice(Context cx,
                                                                   TibcoModel.Type.Schema.ComplexType.Choice choice) {
        List<? extends BallerinaModel.TypeDesc> types = choice.elements().stream()
                .map(element -> {
                    BallerinaModel.TypeDesc typeDesc = cx.getTypeByName(element.ref().name());
                    assert element.maxOccurs() == 1;
                    if (element.minOccurs() == 0) {
                        return BallerinaModel.TypeDesc.UnionTypeDesc.of(typeDesc,
                                BallerinaModel.TypeDesc.BuiltinType.NIL);
                    } else {
                        return typeDesc;
                    }
                })
                .flatMap(type -> {
                    if (type instanceof BallerinaModel.TypeDesc.UnionTypeDesc(
                            Collection<? extends BallerinaModel.TypeDesc> members
                    )) {
                        return members.stream();
                    } else {
                        return Stream.of(type);
                    }
                }).distinct().toList();
        return new BallerinaModel.TypeDesc.UnionTypeDesc(types);
    }

    static class PredefinedTypes {

        private static final BallerinaModel.TypeDesc.BuiltinType ANYDATA = BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
    }
}
