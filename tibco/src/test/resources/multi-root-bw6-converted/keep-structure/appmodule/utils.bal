public type Context record {|
    map<xml> variables;
    xml result;
    Response response?;
    map<SharedVariableContext> sharedVariables;
|};

public type JSONResponse readonly & record {|
    *Response;
    "JSONResponse" kind = "JSONResponse";
    json payload;
|};

public type Response record {|
    "JSONResponse"|"XMLResponse"|"TextResponse" kind;
    anydata payload;
    map<string> headers;
|};

public type SharedVariableContext record {|
    function () returns xml getter;
    function (xml value) setter;
|};

public type TextResponse readonly & record {|
    *Response;
    "TextResponse" kind = "TextResponse";
    string payload;
|};

public type XMLResponse readonly & record {|
    *Response;
    "XMLResponse" kind = "XMLResponse";
    xml payload;
|};

function transform(xml value) returns xml {
    xml result = transformInner(value);
    string str = result.toString();
    return checkpanic xml:fromString(str);
}

function transformInner(xml value) returns xml {
    xml result;
    if (value is xml:Element) {
        result = transformElement(value);
    } else {
        result = value;
    }
    return result;
}

function transformElement(xml:Element element) returns xml {
    XMLElementParseResult parseResult = parseElement(element);
    string? namespace = parseResult.namespace;

    xml:Element transformedElement = element.clone();
    transformedElement.setName(parseResult.name);
    map<string> attributes = transformedElement.getAttributes();
    if namespace != () {
        attributes["xmlns"] = namespace;
    }

    // Get children and transform them recursively
    xml children = element/*.clone();
    xml transformedChildren = children.map(transform);

    // Create new element with transformed children
    transformedElement.setChildren(transformedChildren);
    return transformedElement;
}

function parseElement(xml:Element element) returns XMLElementParseResult {
    string name = element.getName();
    if (name.startsWith("{")) {
        int? index = name.indexOf("}");
        if (index == ()) {
            panic error("Invalid element name: " + name);
        }
        string namespace = name.substring(1, index);
        name = name.substring(index + 1);
        return {namespace: namespace, name: name};
    }
    return {namespace: (), name: name};
}

type XMLElementParseResult record {|
    string? namespace;
    string name;
|};

function addToContext(Context context, string varName, xml value) {
    xml children = value/*;
    xml transformed = xml `<root>${children}</root>`;
    context.variables[varName] = transformed;
    context.result = value;
}
