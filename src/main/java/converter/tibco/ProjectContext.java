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
import java.util.logging.Logger;
import java.util.stream.Stream;

import static ballerina.BallerinaModel.TypeDesc.BuiltinType.ANYDATA;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.BOOLEAN;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.JSON;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.NIL;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.STRING;
import static ballerina.BallerinaModel.TypeDesc.BuiltinType.XML;

import ballerina.BallerinaModel;
import ballerina.BallerinaModel.Statement;
import ballerina.BallerinaModel.Statement.Return;
import ballerina.BallerinaModel.Statement.VarDeclStatment;
import converter.tibco.analyzer.AnalysisResult;
import converter.tibco.analyzer.ModelAnalyser;
import tibco.TibcoModel;

public class ProjectContext {

    private final Map<TibcoModel.Process, ProcessContext> processContextMap = new HashMap<>();
    private final Map<String, Optional<BallerinaModel.ModuleTypeDef>> moduleTypeDefs = new HashMap<>();
    private final List<BallerinaModel.Function> utilityFunctions = new ArrayList<>();
    private final Set<BallerinaModel.Import> utilityFunctionImports = new HashSet<>();
    private final List<String> typeIntrinsics = new ArrayList<>();
    private String toXMLFunction = null;
    private String jsonToXMLFunction = null;
    private String toHttpConfigFunction = null;
    private int nextPort = 8080;
    private int typeCount = 0;
    private int typeAliasCount = 0;
    private int unhandledTypeCount = 0;
    private final ContextWrapperForTypeFile typeCx = new ContextWrapperForTypeFile(this);
    private static final Logger logger = Logger.getLogger(ProjectContext.class.getName());

    ProcessContext getProcessContext(TibcoModel.Process process) {
        return processContextMap.computeIfAbsent(process, p -> new ProcessContext(this, p));
    }

    int allocatePort() {
        return nextPort++;
    }

    public BallerinaModel.Module serialize(Collection<BallerinaModel.TextDocument> textDocuments) {
        List<BallerinaModel.TextDocument> combinedTextDocuments = Stream.concat(textDocuments.stream(),
                Stream.of(typesFile(), utilsFile())).toList();
        logger.info(String.format("Type Statistics - Total Types: %d, Type Aliases: %d, Unhandled Types: %d",
                typeCount, typeAliasCount, unhandledTypeCount));
        return new BallerinaModel.Module("tibco", combinedTextDocuments);
    }

    String getToXmlFunction() {
        if (toXMLFunction == null) {
            importLibraryIfNeeded(Library.XML_DATA);
            String functionName = "toXML";
            utilityFunctions.add(new BallerinaModel.Function(functionName,
                    List.of(new BallerinaModel.Parameter("data", new BallerinaModel.TypeDesc.MapTypeDesc(ANYDATA))),
                    Optional.of("xml"), List.of(new Return<>(
                            Optional.of(new BallerinaModel.Expression.CheckPanic(
                                    new BallerinaModel.Expression.FunctionCall("xmldata:toXml",
                                            new String[] { "data" })))))));
            toXMLFunction = functionName;
        }
        return toXMLFunction;
    }

    String getJsonToXMLFunction() {
        if (jsonToXMLFunction == null) {
            importLibraryIfNeeded(Library.XML_DATA);
            String functionName = "fromJson";
            utilityFunctions.add(
                    new BallerinaModel.Function(functionName, List.of(new BallerinaModel.Parameter("data", JSON)),
                            "xml", List.of(new Return<>(Optional.of(
                                    new BallerinaModel.Expression.CheckPanic(
                                            new BallerinaModel.Expression.FunctionCall("xmldata:fromJson",
                                                    new String[] { "data" })))))));
            jsonToXMLFunction = functionName;
        }
        return jsonToXMLFunction;
    }

    String getParseHttpConfigFunction() {
        if (toHttpConfigFunction == null) {
            BallerinaModel.TypeDesc targetType = getHttpConfigType();
            toHttpConfigFunction = createConvertToTypeFunction(targetType);
        }
        return toHttpConfigFunction;
    }

    BallerinaModel.TypeDesc.TypeReference getHttpConfigType() {
        // type HTTPRequestConfig record {
        // string Method;
        // string RequestURI;
        // json PostData = "";
        // map<string> Headers = {};
        // map<string> parameters = {};
        // };
        String httpConfigTy = "HTTPRequestConfig";
        if (moduleTypeDefs.containsKey(httpConfigTy)) {
            return new BallerinaModel.TypeDesc.TypeReference(httpConfigTy);
        }
        BallerinaModel.ModuleTypeDef httpConfigType = new BallerinaModel.ModuleTypeDef(httpConfigTy,
                new BallerinaModel.TypeDesc.RecordTypeDesc(
                        List.of(
                                new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("Method", STRING),
                                new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("RequestURI", STRING),
                                // TODO: handle put
                                new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("PostData", JSON, Optional.of(
                                        new BallerinaModel.Expression.StringConstant(""))),
                                new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("Headers",
                                        new BallerinaModel.TypeDesc.MapTypeDesc(STRING), Optional.of(
                                                new BallerinaModel.Expression.MappingConstructor(List.of()))),
                                new BallerinaModel.TypeDesc.RecordTypeDesc.RecordField("parameters",
                                        new BallerinaModel.TypeDesc.MapTypeDesc(STRING), Optional.of(
                                                new BallerinaModel.Expression.MappingConstructor(List.of()))))));
        moduleTypeDefs.put(httpConfigTy, Optional.of(httpConfigType));
        typeIntrinsics.add(Intrinsics.CREATE_HTTP_REQUEST_PATH_FROM_CONFIG.body);

        return new BallerinaModel.TypeDesc.TypeReference(httpConfigTy);
    }

    private void importLibraryIfNeeded(Library library) {
        utilityFunctionImports.add(new BallerinaModel.Import("ballerina", library.value, Optional.empty()));
    }

    String createConvertToTypeFunction(BallerinaModel.TypeDesc targetType) {
        String functionName = "convertTo" + ConversionUtils.sanitizes(targetType.toString());
        importLibraryIfNeeded(Library.XML_DATA);
        BallerinaModel.Expression.FunctionCall parseAsTypeCall = new BallerinaModel.Expression.FunctionCall(
                "xmldata:parseAsType", new String[] { "input" });
        BallerinaModel.Expression.CheckPanic checkPanic = new BallerinaModel.Expression.CheckPanic(parseAsTypeCall);
        Return<BallerinaModel.Expression.CheckPanic> returnStmt = new Return<>(Optional.of(checkPanic));
        BallerinaModel.Function function = new BallerinaModel.Function(functionName,
                List.of(new BallerinaModel.Parameter("input", XML)), Optional.of(targetType.toString()),
                List.of(returnStmt));
        utilityFunctions.add(function);
        return functionName;
    }

    private BallerinaModel.TextDocument utilsFile() {
        List<BallerinaModel.Import> imports = utilityFunctionImports.stream().toList();
        return new BallerinaModel.TextDocument("utils.bal", imports, List.of(), List.of(), List.of(), List.of(),
                utilityFunctions, List.of());
    }

    private BallerinaModel.TextDocument typesFile() {
        List<BallerinaModel.ModuleTypeDef> typeDefs = new ArrayList<>();
        for (Map.Entry<String, Optional<BallerinaModel.ModuleTypeDef>> entry : moduleTypeDefs.entrySet()) {
            if (entry.getValue().isPresent()) {
                typeDefs.add(entry.getValue().get());
            } else {
                logger.warning(
                        String.format("Type definition not found for %s using `anydata` as fallback", entry.getKey()));
                typeDefs.add(new BallerinaModel.ModuleTypeDef(entry.getKey(), ANYDATA));
            }
        }
        List<BallerinaModel.Import> imports = typeCx.imports.stream().toList();
        return new BallerinaModel.TextDocument("types.bal", imports, typeDefs, List.of(), List.of(), List.of(),
                List.of(), List.of(), typeIntrinsics);
    }

    FunctionData getProcessStartFunction(String processName) {
        TibcoModel.Process process = processContextMap.keySet().stream().filter(proc -> proc.name().equals(processName))
                .findAny()
                .orElseThrow(() -> new IndexOutOfBoundsException("failed to find process" + processName));
        return getProcessContext(process).getProcessStartFunction();
    }

    BallerinaModel.TypeDesc getTypeByName(String name, ContextWithFile cx) {
        // TODO: how to handle names spaces
        name = ConversionUtils.sanitizes(XmlToTibcoModelConverter.getTagNameWithoutNameSpace(name));
        if (moduleTypeDefs.containsKey(name)) {
            return new BallerinaModel.TypeDesc.TypeReference(name);
        }
        if (cx.hasConstantWithName(name)) {
            return new BallerinaModel.TypeDesc.TypeReference(name);
        }

        Optional<BallerinaModel.TypeDesc.BuiltinType> builtinType = mapToBuiltinType(name);
        if (builtinType.isPresent()) {
            return builtinType.get();
        }

        Optional<BallerinaModel.TypeDesc.TypeReference> libraryType = mapToLibraryType(cx, name);
        if (libraryType.isPresent()) {
            return libraryType.get();
        }

        if (!moduleTypeDefs.containsKey(name)) {
            moduleTypeDefs.put(name, Optional.empty());
        }
        return new BallerinaModel.TypeDesc.TypeReference(name);
    }

    private Optional<BallerinaModel.TypeDesc.TypeReference> mapToLibraryType(ContextWithFile cx, String name) {
        return switch (name) {
            case "client4XXError" -> Optional.of(getLibraryType(cx, Library.HTTP, "NotFound"));
            case "server5XXError" -> Optional.of(getLibraryType(cx, Library.HTTP, "InternalServerError"));
            case "Client" -> Optional.of(getLibraryType(cx, Library.HTTP, "Client"));
            default -> Optional.empty();
        };
    }

    private BallerinaModel.TypeDesc.TypeReference getLibraryType(ContextWithFile cx, Library library, String typeName) {
        cx.addLibraryImport(library);
        return new BallerinaModel.TypeDesc.TypeReference(library.value + ":" + typeName);
    }

    private Optional<BallerinaModel.TypeDesc.BuiltinType> mapToBuiltinType(String name) {
        return switch (name) {
            case "string" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.STRING);
            case "integer", "int", "long" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.INT);
            case "anydata" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.ANYDATA);
            case "xml" -> Optional.of(XML);
            case "null" -> Optional.of(NIL);
            case "boolean" -> Optional.of(BOOLEAN);
            // TODO: handle base64Binary
            case "base64Binary" -> Optional.of(BallerinaModel.TypeDesc.BuiltinType.INT);
            default -> Optional.empty();
        };
    }

    ContextWithFile getTypeContext() {
        return typeCx;
    }

    boolean addModuleTypeDef(String name, BallerinaModel.ModuleTypeDef moduleTypeDef) {
        if (moduleTypeDef.typeDesc() instanceof BallerinaModel.TypeDesc.TypeReference(String name1) &&
                name1.equals(name)) {
            return false;
        }
        this.moduleTypeDefs.put(name, Optional.of(moduleTypeDef));
        return true;
    }

    public void incrementTypeCount() {
        typeCount++;
    }

    public void incrementUnhandledTypeCount() {
        incrementTypeCount();
        unhandledTypeCount++;
    }

    public void incrementTypeAliasCount() {
        incrementTypeCount();
        typeAliasCount++;
    }

    record FunctionData(String name, BallerinaModel.TypeDesc inputType, BallerinaModel.TypeDesc returnType) {

        FunctionData {
            assert name != null && !name.isEmpty();
            assert inputType != null;
            assert returnType != null;
        }
    }

    private static class ContextWrapperForTypeFile implements ContextWithFile {

        final Set<BallerinaModel.Import> imports = new HashSet<>();
        final ProjectContext cx;

        private ContextWrapperForTypeFile(ProjectContext cx) {
            this.cx = cx;
        }

        @Override
        public boolean hasConstantWithName(String name) {
            return false;
        }

        @Override
        public void addLibraryImport(Library library) {
            imports.add(new BallerinaModel.Import("ballerina", library.value, Optional.empty()));
        }

        @Override
        public BallerinaModel.TypeDesc getTypeByName(String name) {
            return cx.getTypeByName(name, this);
        }

        @Override
        public boolean addModuleTypeDef(String name, BallerinaModel.ModuleTypeDef defn) {
            return cx.addModuleTypeDef(name, defn);
        }

        @Override
        public ProjectContext getProjectContext() {
            return cx;
        }
    }
}
