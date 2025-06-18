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

package common;

import io.ballerina.compiler.syntax.tree.ModuleMemberDeclarationNode;
import io.ballerina.compiler.syntax.tree.SyntaxTree;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Stream;

public final class BICodeConverter {
    public static final Predicate<BallerinaModel.ModuleVar> DEFAULT_IS_CONFIGURABLE_PREDICATE =
            BallerinaModel.ModuleVar::isConfigurable;

    public static final Predicate<BallerinaModel.ModuleVar> DEFAULT_IS_CONNECTION_PREDICATE = new TypeNamePredicate(
            Set.of("http:Client", "jdbc:Client", "mysql:Client", "oracledb:Client", "jms:Connection"));
    public static final Predicate<BallerinaModel.TextDocument> DEFAULT_SKIP_CONVERSION_PREDICATE = ignored -> false;

    private final Predicate<BallerinaModel.ModuleVar> isConfigurable;
    private final Predicate<BallerinaModel.ModuleVar> isConnection;
    private final Predicate<BallerinaModel.TextDocument> skipConversion;

    public BICodeConverter(Predicate<BallerinaModel.ModuleVar> isConfigurable,
                           Predicate<BallerinaModel.ModuleVar> isConnection,
                           Predicate<BallerinaModel.TextDocument> skipConversion) {
        this.isConfigurable = isConfigurable;
        this.isConnection = isConnection;
        this.skipConversion = skipConversion;
    }

    public BICodeConverter() {
        this(DEFAULT_IS_CONFIGURABLE_PREDICATE, DEFAULT_IS_CONNECTION_PREDICATE, DEFAULT_SKIP_CONVERSION_PREDICATE);
    }

    public BallerinaModel.Module convert(BallerinaModel.Module module) {
        Stream<BallerinaModel.TextDocument> skipped = module.textDocuments().stream().filter(skipConversion);
        BallerinaModel.TextDocument main = fixImports(mergeWithExistingIfNeeded(mainFile(module), module));
        BallerinaModel.TextDocument configs = fixImports(mergeWithExistingIfNeeded(configs(module), module));
        BallerinaModel.TextDocument connections = fixImports(mergeWithExistingIfNeeded(connections(module), module));
        BallerinaModel.TextDocument types = fixImports(mergeWithExistingIfNeeded(types(module), module));
        BallerinaModel.TextDocument functions = fixImports(mergeWithExistingIfNeeded(functions(module), module));
        Stream<BallerinaModel.TextDocument> comments =
                extractDocComments(module).isEmpty() ? Stream.empty() :
                        Stream.of(fixImports(mergeWithExistingIfNeeded(comments(module), module)));
        List<BallerinaModel.TextDocument> docs =
                Stream.concat(Stream.concat(
                                        Stream.of(main, configs, connections, types, functions),
                                        skipped),
                                comments)
                        .toList();
        return new BallerinaModel.Module(module.name(), docs);
    }

    private BallerinaModel.TextDocument configs(BallerinaModel.Module module) {
        List<BallerinaModel.ModuleVar> configs = extractConfigurable(module);
        return new BallerinaModel.TextDocument("configs.bal", List.of(), List.of(), configs,
                List.of(), List.of(), List.of(), List.of());
    }

    private BallerinaModel.TextDocument connections(BallerinaModel.Module module) {
        List<BallerinaModel.ModuleVar> connections = extractConnections(module);
        return new BallerinaModel.TextDocument("connections.bal", List.of(), List.of(), connections,
                List.of(), List.of(), List.of(), List.of());
    }

    private BallerinaModel.TextDocument mainFile(BallerinaModel.Module module) {
        List<BallerinaModel.Service> services = extractServices(module);
        List<BallerinaModel.Listener> listeners = extractListeners(module);
        List<String> intrinsics = extractNamespaceInrinsics(module);
        // uncategorized module vars are kept in main file.
        List<BallerinaModel.ModuleVar> moduleVars = extractUncategorizedModuleVars(module);

        return new BallerinaModel.TextDocument("main.bal", List.of(), List.of(), moduleVars,
                listeners, services, List.of(), List.of(), intrinsics, List.of());
    }

    private BallerinaModel.TextDocument comments(BallerinaModel.Module module) {
        List<String> comments = extractDocComments(module);
        return new BallerinaModel.TextDocument("todo.bal", List.of(), List.of(), List.of(),
                List.of(), List.of(), List.of(), comments);
    }

    private BallerinaModel.TextDocument types(BallerinaModel.Module module) {
        List<BallerinaModel.ModuleTypeDef> types = extractTypeDefinitions(module);
        List<String> intrinsics = extractTypeInrinsics(module);
        return new BallerinaModel.TextDocument("types.bal", List.of(), types, List.of(),
                List.of(), List.of(), List.of(), List.of(), intrinsics, List.of());
    }

    private BallerinaModel.TextDocument functions(BallerinaModel.Module module) {
        List<BallerinaModel.Function> functions = extractFunctions(module);
        List<String> intrinsics = extractFunctionIntrinsics(module);
        return new BallerinaModel.TextDocument("functions.bal", List.of(), List.of(), List.of(),
                List.of(), List.of(), functions, List.of(), intrinsics, List.of());
    }

    private BallerinaModel.TextDocument fixImports(BallerinaModel.TextDocument doc) {
        // TODO: we can do this better by visiting tree and figure out types and
        //  function calls. But should be good enough for now
        SyntaxTree st = new CodeGenerator(doc).generateSyntaxTree();
        String content = st.toSourceCode();
        Pattern searchPattern = Pattern.compile("([a-zA-Z0-9]+):[a-zA-Z0-9]+");

        // Find all matches in content
        Matcher matcher = searchPattern.matcher(content);
        Set<BallerinaModel.Import> imports = new HashSet<>();

        while (matcher.find()) {
            imports.addAll(prefixToImport(matcher.group(1)));
        }

        List<BallerinaModel.Import> combinedImports = Stream.concat(doc.imports().stream(), imports.stream()).toList();
        return new BallerinaModel.TextDocument(doc.documentName(), combinedImports, doc.moduleTypeDefs(),
                doc.moduleVars(), doc.listeners(), doc.services(), doc.functions(), doc.Comments(), doc.intrinsics(),
                doc.astNodes());
    }

    private List<BallerinaModel.Import> prefixToImport(String prefix) {
        return switch (prefix) {
            case "http" -> List.of(new BallerinaModel.Import("ballerina", "http"));
            case "xslt" -> List.of(new BallerinaModel.Import("ballerina", "xslt"));
            case "xmldata" -> List.of(new BallerinaModel.Import("ballerina", "data.xmldata"));
            case "jsondata" -> List.of(new BallerinaModel.Import("ballerina", "data.jsondata"));
            case "java.jdbc", "jdbc" -> List.of(new BallerinaModel.Import("ballerinax", "java.jdbc"));
            case "java.jms", "jms" -> List.of(new BallerinaModel.Import("ballerinax", "java.jms"));
            case "io" -> List.of(new BallerinaModel.Import("ballerina", "io"));
            case "log" -> List.of(new BallerinaModel.Import("ballerina", "log"));
            case "soap11" -> List.of(new BallerinaModel.Import("ballerina", "soap.soap11"));
            case "sql" -> List.of(new BallerinaModel.Import("ballerina", "sql"));
            case "mysql" -> List.of(
                    new BallerinaModel.Import("ballerinax", "mysql"),
                    new BallerinaModel.Import("ballerinax", "mysql.driver", Optional.of("_"))
            );
            case "oracledb" -> List.of(
                    new BallerinaModel.Import("ballerinax", "oracledb"),
                    new BallerinaModel.Import("ballerinax", "oracledb.driver", Optional.of("_"))
            );
            default -> List.of();
        };
    }

    private BallerinaModel.TextDocument mergeWithExistingIfNeeded(BallerinaModel.TextDocument doc,
                                                                  BallerinaModel.Module existing) {
        String name = doc.documentName();
        return existing.textDocuments().stream()
                .filter(each -> each.documentName().equalsIgnoreCase(name))
                .filter(skipConversion)
                .findAny()
                .map(current -> mergeTextDocuments(current, doc))
                .orElse(doc);
    }

    private BallerinaModel.TextDocument mergeTextDocuments(BallerinaModel.TextDocument doc1,
                                                           BallerinaModel.TextDocument doc2) {
        assert doc1.documentName().equals(doc2.documentName());
        List<BallerinaModel.Import> imports = mergeList(doc1.imports(), doc2.imports());
        List<BallerinaModel.ModuleTypeDef> moduleTypeDefs = mergeList(doc1.moduleTypeDefs(), doc2.moduleTypeDefs());
        List<BallerinaModel.ModuleVar> moduleVars = mergeList(doc1.moduleVars(), doc2.moduleVars());
        List<BallerinaModel.Listener> listeners = mergeList(doc1.listeners(), doc2.listeners());
        List<BallerinaModel.Service> services = mergeList(doc1.services(), doc2.services());
        List<BallerinaModel.Function> functions = mergeList(doc1.functions(), doc2.functions());
        List<String> comments = mergeList(doc1.Comments(), doc2.Comments());
        List<String> intrinsics = mergeList(doc1.intrinsics(), doc2.intrinsics());
        List<ModuleMemberDeclarationNode> astNodes = mergeList(doc1.astNodes(), doc2.astNodes());
        return new BallerinaModel.TextDocument(doc1.documentName(), imports, moduleTypeDefs, moduleVars,
                listeners, services, functions, comments, intrinsics, astNodes);
    }

    private <E> List<E> mergeList(List<E> list1, List<E> list2) {
        return Stream.concat(list1.stream(), list2.stream()).toList();
    }

    private List<BallerinaModel.ModuleVar> extractConfigurable(BallerinaModel.Module module) {
        return getTextDocumentStream(module).flatMap(doc -> doc.moduleVars().stream())
                .filter(isConfigurable).toList();
    }

    private List<BallerinaModel.ModuleVar> extractConnections(BallerinaModel.Module module) {
        return getTextDocumentStream(module).flatMap(doc -> doc.moduleVars().stream())
                .filter(isConnection).toList();
    }

    private List<BallerinaModel.ModuleVar> extractUncategorizedModuleVars(BallerinaModel.Module module) {
        Predicate<BallerinaModel.ModuleVar> isConfigurableOrConnection = isConfigurable.or(isConnection);
        return getTextDocumentStream(module).flatMap(doc -> doc.moduleVars().stream())
                .filter(isConfigurableOrConnection.negate()).toList();
    }

    private List<BallerinaModel.Service> extractServices(BallerinaModel.Module module) {
        return getTextDocumentStream(module).flatMap(doc -> doc.services().stream())
                .toList();
    }

    private List<BallerinaModel.Listener> extractListeners(BallerinaModel.Module module) {
        return getTextDocumentStream(module).flatMap(doc -> doc.listeners().stream())
                .toList();
    }

    private List<String> extractDocComments(BallerinaModel.Module module) {
        return getTextDocumentStream(module).flatMap(doc -> doc.Comments().stream())
                .toList();
    }

    private List<BallerinaModel.ModuleTypeDef> extractTypeDefinitions(BallerinaModel.Module module) {
        return getTextDocumentStream(module).flatMap(doc -> doc.moduleTypeDefs().stream())
                .toList();
    }

    private List<BallerinaModel.Function> extractFunctions(BallerinaModel.Module module) {
        return getTextDocumentStream(module).flatMap(doc -> doc.functions().stream()).toList();
    }

    private boolean isTypeIntrinsic(String intrinsic) {
        return intrinsic.trim().startsWith("type ");
    }

    private boolean isNamespaceIntrinsic(String intrinsic) {
        return intrinsic.trim().startsWith("xmlns ");
    }

    private boolean isFunctionIntrinsic(String intrinsic) {
        return !isTypeIntrinsic(intrinsic) && !isNamespaceIntrinsic(intrinsic);
    }

    private List<String> extractFunctionIntrinsics(BallerinaModel.Module module) {
        return getTextDocumentStream(module).flatMap(doc -> doc.intrinsics().stream()).distinct().
                filter(this::isFunctionIntrinsic).toList();
    }

    private List<String> extractTypeInrinsics(BallerinaModel.Module module) {
        return getTextDocumentStream(module).flatMap(doc -> doc.intrinsics().stream()).distinct().
                filter(this::isTypeIntrinsic).toList();
    }

    private List<String> extractNamespaceInrinsics(BallerinaModel.Module module) {
        return getTextDocumentStream(module).flatMap(doc -> doc.intrinsics().stream()).distinct().
                filter(this::isNamespaceIntrinsic).toList();
    }

    private @NotNull Stream<BallerinaModel.TextDocument> getTextDocumentStream(BallerinaModel.Module module) {
        return module.textDocuments().stream().filter(Predicate.not(skipConversion));
    }

    private record TypeNamePredicate(Collection<String> typeNames) implements Predicate<BallerinaModel.ModuleVar> {

        @Override
        public boolean test(BallerinaModel.ModuleVar moduleVar) {
            return typeNames.stream().anyMatch(each -> moduleVar.type().equals(each));
        }
    }
}
