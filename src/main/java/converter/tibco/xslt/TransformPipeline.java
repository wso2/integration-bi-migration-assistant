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

package converter.tibco.xslt;

import converter.tibco.ActivityContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.List;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class TransformPipeline {

    List<Transform> transforms = new ArrayList<>();

    public void append(Transform transform) {
        transforms.add(transform);
    }

    public String apply(ActivityContext cx, String xsltContent) {
        String result = xsltContent;
        for (Transform transform : transforms) {
            result = apply(cx, result, transform);
        }
        return result;
    }

    private static String apply(ActivityContext acx, String xsltContent, Transform transform) {
        TransformContext cx = new TransformContext(acx, xsltContent);
        AnalysisResult analysisResult = cx.getAnalysisResult();
        StringBuilder sb = new StringBuilder();
        Collection<ChunkData> paths = analysisResult.parse().stream()
                .map(path -> new ChunkData(path, transform::transformPath))
                .toList();
        Collection<ChunkData> parameterUsages = analysisResult.parameterUsage().stream()
                .map(path -> new ChunkData(path, transform::transformParameterUsage))
                .toList();
        Collection<ChunkData> parameters = analysisResult.parameters().stream()
                .map(path -> new ChunkData(path, transform::transformParameter))
                .toList();
        for (Segment segment : segments(xsltContent, paths, parameterUsages, parameters)) {
            sb.append(segment.transformFn.apply(cx, segment.value));
        }
        String result = sb.toString();
        return transform.transform(cx, result);
    }

    record ChunkData(AnalysisResult.Chunk chunk, BiFunction<TransformContext, String, String> fn) {

    }

    private static List<Segment> segments(String content,
                                          Collection<ChunkData> paths, Collection<ChunkData> parameterUsages,
                                          Collection<ChunkData> parameters) {
        ArrayList<ChunkData> sortedChunks =
                Stream.of(paths, parameterUsages, parameters).flatMap(Collection::stream)
                        .sorted(Comparator.comparingInt(each -> each.chunk.startPos()))
                        .collect(Collectors.toCollection(ArrayList::new));
        int startPos = 0;
        List<Segment> segments = new ArrayList<>();
        for (ChunkData data : sortedChunks) {
            AnalysisResult.Chunk chunk = data.chunk;
            assert chunk.value().equals(content.substring(chunk.startPos(), chunk.endPos()));
            if (chunk.startPos() > startPos) {
                segments.add(Segment.noOp(content.substring(startPos, chunk.startPos())));
            }
            segments.add(new Segment(chunk.value(), data.fn));
            startPos = chunk.endPos();
        }
        if (startPos < content.length()) {
            segments.add(Segment.noOp(content.substring(startPos)));
        }
        return segments;
    }

    private record Segment(String value, BiFunction<TransformContext, String, String> transformFn) {

        static Segment noOp(String value) {
            return new Segment(value, (cx, s) -> s);
        }

    }

}
