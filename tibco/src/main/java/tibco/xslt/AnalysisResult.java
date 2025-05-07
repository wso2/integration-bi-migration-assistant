/*
 *  Copyright (c) 2025, WSO2 LLC. (http://www.wso2.com). All Rights Reserved.
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

package tibco.xslt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.function.Predicate;

record AnalysisResult(Collection<Chunk> parameters, Collection<Chunk> paths, Collection<Chunk> parameterUsage) {

    AnalysisResult {
        parameters = Collections.unmodifiableCollection(parameters);
        paths = Collections.unmodifiableCollection(paths);
        parameterUsage = Collections.unmodifiableCollection(parameterUsage);
    }

    record Chunk(String value, int startPos, int endPos) {

    }

    private record ParseResult(Collection<Chunk> paths, Collection<Chunk> parameterUsage) {

        ParseResult {
            paths = Collections.unmodifiableCollection(paths);
            parameterUsage = Collections.unmodifiableCollection(parameterUsage);
        }
    }


    private static final Collection<String> XPATH_ATTRIBUTES =
            Set.of("select", "test", "match", "xpath", "from", "count");

    static AnalysisResult analyse(String xsltContent) {
        Collection<Chunk> parameters = parameters(xsltContent);
        ParseResult result = parse(xsltContent);
        return new AnalysisResult(parameters, result.paths(), result.parameterUsage());
    }

    static Collection<Chunk> parameters(String xsltContent) {
        List<Chunk> parameterChunks = new java.util.ArrayList<>();
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile("<\\w+:param\\s+name=[\"'](.*?)[\"']");
        java.util.regex.Matcher matcher = pattern.matcher(xsltContent);
        while (matcher.find()) {
            parameterChunks.add(new Chunk(matcher.group(1), matcher.start(1), matcher.end(1)));
        }
        return Collections.unmodifiableList(parameterChunks);
    }

    private static ParseResult parse(String xsltContent) {
        int index = 0;
        List<Chunk> paths = new ArrayList<>();
        List<Chunk> parameters = new ArrayList<>();
        boolean inTag = false;
        int start = -1;
        while (index < xsltContent.length()) {
            char c = xsltContent.charAt(index);
            switch (c) {
                case '<' -> {
                    inTag = true;
                    start = index;
                    index++;
                }
                case '/' -> {
                    index++;
                    if (xsltContent.charAt(index) == '>') {
                        index++;
                        inTag = false;
                        ParseResult parseResult = parseTag(xsltContent.substring(start, index), start);
                        paths.addAll(parseResult.paths());
                        parameters.addAll(parseResult.parameterUsage());
                    }
                }
                case '>' -> {
                    index++;
                    if (inTag) {
                        inTag = false;
                        ParseResult parseResult = parseTag(xsltContent.substring(start, index), start);
                        paths.addAll(parseResult.paths());
                        parameters.addAll(parseResult.parameterUsage());
                    }
                }
                default -> index++;

            }
        }
        return new ParseResult(paths, parameters);
    }

    static ParseResult parseTag(String tagDefn, int offset) {
        assert tagDefn.startsWith("<") && tagDefn.endsWith(">");
        int index = 1;
        index = skipSpaces(tagDefn, index);
        // seek till end of tag name
        index = incrementWhile(tagDefn, index, Predicate.not(Character::isWhitespace));
        index = skipSpaces(tagDefn, index);
        List<Chunk> paths = new ArrayList<>();
        List<Chunk> parameters = new ArrayList<>();
        // now we are at attributes
        while (index < tagDefn.length()) {
            if (tagDefn.charAt(index) == '/' || tagDefn.charAt(index) == '>') {
                break;
            }
            if (tagDefn.charAt(index) == '?' && tagDefn.charAt(index + 1) == '>') {
                break;
            }
            int attributeNameEnd =
                    incrementWhile(tagDefn, index, Predicate.not((ch) -> Character.isWhitespace(ch) || ch == '='));
            String attributeName = tagDefn.substring(index, attributeNameEnd);
            assert !attributeName.isBlank();
            index = attributeNameEnd;

            index = skipSpaces(tagDefn, index);
            assert tagDefn.charAt(index) == '=';
            index++;
            index = skipSpaces(tagDefn, index);
            assert tagDefn.charAt(index) != '/' && tagDefn.charAt(index) != '>';
            Optional<Character> stringTerminator;
            if (tagDefn.charAt(index) == '\'' || tagDefn.charAt(index) == '"') {
                stringTerminator = Optional.of(tagDefn.charAt(index));
                index++;
            } else {
                stringTerminator = Optional.empty();
            }
            int attributeValueEnd =
                    incrementWhile(tagDefn, index,
                            Predicate.not((ch) -> stringTerminator.map(c -> c.equals(ch)).orElse(false) || ch == '>'));
            String attributeValue = tagDefn.substring(index, attributeValueEnd);
            if (XPATH_ATTRIBUTES.contains(attributeName)) {
                ParseResult parseResult = parseXPath(attributeValue, index + offset);
                paths.addAll(parseResult.paths());
                parameters.addAll(parseResult.parameterUsage());
            }
            index = attributeValueEnd + 1;
            index = skipSpaces(tagDefn, index);
        }
        return new ParseResult(paths, parameters);
    }

    private static int skipSpaces(String value, int index) {
        return incrementWhile(value, index, Character::isWhitespace);
    }

    private static int incrementWhile(String value, int startIndex, Predicate<Character> predicate) {
        int index = startIndex;
        while (index < value.length() && predicate.test(value.charAt(index))) {
            index++;
        }
        return index;
    }

    static ParseResult parseXPath(String xPath, int offset) {
        List<Chunk> paths = new ArrayList<>();
        List<Chunk> parameters = new ArrayList<>();
        int index = 0;
        int start = -1;
        int paranthesisCount = 0;
        boolean insidePath = false;
        while (index < xPath.length()) {
            char c = xPath.charAt(index);
            switch (c) {
                case '\'' -> {
                    index = incrementWhile(xPath, index + 1, Predicate.not((chr) -> chr.equals('\'')));
                    continue;
                }
                case '$' -> {
                    int paramStart = index;
                    index = incrementWhile(xPath, index + 1, Predicate.not((chr) -> chr.equals('/')));
                    parameters.add(new Chunk(xPath.substring(paramStart, index), paramStart + offset, index + offset));
                    start = index;
                    insidePath = true;
                }
                case '(' -> {
                    if (insidePath) {
                        paranthesisCount++;
                    }
                }
                case ')' -> {
                    if (insidePath) {
                        paranthesisCount--;
                        if (paranthesisCount < 0) {
                            paths.add(
                                    new Chunk(xPath.substring(start, index), start + offset, index + offset));
                            insidePath = false;
                            paranthesisCount = 0;
                        }
                    }
                }
                case '/' -> {
                    if (!insidePath) {
                        insidePath = true;
                        start = index;
                    }
                }
                case ' ' | ',' -> {
                    if (insidePath) {
                        paths.add(new Chunk(xPath.substring(start, index), start + offset, index + offset));
                        insidePath = false;
                    }
                }
            }
            index++;
        }
        // We have bound check to handle $foo (TODO: need to think of a better way)
        if (insidePath && index == xPath.length()) {
            paths.add(new Chunk(xPath.substring(start, index), start + offset, index + offset));
        }

        return new ParseResult(paths, parameters);
    }

}
