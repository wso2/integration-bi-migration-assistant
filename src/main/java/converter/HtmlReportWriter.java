package converter;

import dataweave.converter.DWConstruct;
import dataweave.converter.DWConversionStats;
import mule.MuleXMLTag;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.LinkedHashMap;
import java.util.Locale;
import java.util.logging.Logger;

public class HtmlReportWriter {

    private static final String COMPATIBLE_TAG_TYPE = "Compatible";
    private static final String INCOMPATIBLE_TAG_TYPE = "Incompatible";

    public static int writeHtmlReport(Logger logger, Path reportFilePath,
                                      LinkedHashMap<String, Integer> xmlCompatibleTagCountMap,
                                      LinkedHashMap<String, Integer> xmlIncompatibleTagCountMap,
                                      DWConversionStats dwStats) {
        String htmlHeader = generateHtmlHeader();

        /// Percentage calculation
        int totalCompatibleTagWeight = calculateTotalWeight(xmlCompatibleTagCountMap);
        int totalIncompatibleTagWeight = calculateTotalWeight(xmlIncompatibleTagCountMap);
        int xmlTotalWeight = totalCompatibleTagWeight + totalIncompatibleTagWeight;

        int dwTotalWeight = dwStats.getTotalWeight();
        int dwConvertedWeight = dwStats.getConvertedWeight();

        // Overall weight and conversion %
        int combinedTotalWeight = xmlTotalWeight + dwTotalWeight;
        int combinedConvertedWeight = totalCompatibleTagWeight + dwConvertedWeight;
        int percentage = combinedTotalWeight == 0 ? 0 : combinedConvertedWeight * 100 / combinedTotalWeight;

        String conversionPercentageSection = generateConversionPercentageSection(percentage);

        int compatibleTagCount = xmlCompatibleTagCountMap.size();
        int incompatibleTagCount = xmlIncompatibleTagCountMap.size();
        String compatibleSummarySection = generateSummarySection(COMPATIBLE_TAG_TYPE, compatibleTagCount,
                totalCompatibleTagWeight);
        String compatibleTagTable = generateTagsSection(COMPATIBLE_TAG_TYPE, xmlCompatibleTagCountMap,
                "compatibleTagsDrawer", "compatibleArrow", true);
        String incompatibleSummarySection = generateSummarySection(INCOMPATIBLE_TAG_TYPE, incompatibleTagCount,
                totalIncompatibleTagWeight);
        String incompatibleTagTable = generateTagsSection(INCOMPATIBLE_TAG_TYPE, xmlIncompatibleTagCountMap,
                "incompatibleTagsDrawer", "incompatibleArrow", false);

        String dwSummarySection = generateSummarySection("DataWeave", dwStats.getEncountered().size(),
                dwTotalWeight);
        String dwTagsTable = generateDataWeaveTagsSection(dwStats);

        String tagWeightsSection = generateTagWeightReferenceTableSection() +
                generateDWConstructWeightReferenceTableSection();
        String htmlFooter = generateHtmlFooter();

        String reportContent = htmlHeader + conversionPercentageSection +
                compatibleSummarySection + compatibleTagTable +
                incompatibleSummarySection + incompatibleTagTable +
                dwSummarySection + dwTagsTable +
                tagWeightsSection + htmlFooter;

        try {
            Files.writeString(reportFilePath, reportContent);
            logger.info("Incompatible elements report written to " + reportFilePath);
        } catch (IOException e) {
            logger.severe("Error writing report to file: " + e.getMessage());
        }
        return percentage;
    }

    private static String generateHtmlHeader() {
        return """
                <!DOCTYPE html>
                <html>
                <head>
                <title>Migration Summary</title>
                <style>
                body { font-family: Arial, sans-serif; background-color: #f4f4f9; color: #333; }
                table { width: 100%; border-collapse: collapse; margin: 20px 0; }
                th, td { border: 1px solid #ddd; padding: 12px; text-align: left; }
                th { background-color: #FF6347; color: white; }
                tr:nth-child(even) { background-color: #f2f2f2; }
                tr:hover { background-color: #ddd; }
                h1 { text-align: center; }
                footer { text-align: center; margin-top: 20px; font-size: 0.9em; color: #666; }
                .drawer { overflow: hidden; transition: max-height 0.3s ease-out; max-height: 0; }
                .drawer.open { max-height: 500px; }
                .drawer-toggle { cursor: pointer; display: flex; align-items: center; }
                .drawer-toggle span { margin-right: 5px; }
                .arrow { transition: transform 0.3s ease-out; display: inline-block; width: 10px;
                 height: 10px; border-right: 2px solid #333; border-bottom: 2px solid #333; transform: rotate(45deg); }
                .arrow.open { transform: rotate(135deg); }
                .summary-container { background-color: #fff; padding: 20px; border-radius: 8px;
                 box-shadow: 0 0 10px rgba(0, 0, 0, 0.1); margin: 20px 0; }
                .summary-item { display: flex; align-items: center; margin-bottom: 10px; }
                .summary-item:last-child { margin-bottom: 0; }
                .summary-bullet { margin-right: 10px; font-size: 1.5em; color: #FF6347; }
                .green-table th { background-color: #66CDAA; color: white; }
                .green-table tr:nth-child(even) { background-color: #e6f7f1; }
                .green-table tr:hover { background-color: #cceee3; }
                .blue-table th { background-color: #4682B4; color: white; }
                .blue-table tr:nth-child(even) { background-color: #e0f0ff; }
                .blue-table tr:hover { background-color: #b0d4f1; }
                .scrollable-table { max-height: 300px; overflow-y: auto; display: block; }
                </style>
                </head>
                <body>
                <h1>Migration Summary</h1>
                """;
    }

    private static String generateConversionPercentageSection(int percentage) {
        return String.format("<h3>Conversion Percentage: %d%%</h3>\n", percentage);
    }

    private static String generateSummarySection(String tagType, int tagCount, int totalTagWeight) {
        return "<div class=\"summary-container\">\n" +
                "<div class=\"summary-item\">\n" +
                "<span class=\"summary-bullet\">&#8226;</span>\n" +
                String.format("<p>%s XML Element Tag Count: %d</p>\n", tagType, tagCount) +
                "</div>\n" +
                "<div class=\"summary-item\">\n" +
                "<span class=\"summary-bullet\">&#8226;</span>\n" +
                String.format("<p>%s XML Element Tag Weight: %d</p>\n", tagType, totalTagWeight) +
                "</div>\n" +
                "</div>\n";
    }

    private static String generateTagsSection(String tagType, LinkedHashMap<String, Integer> tagMap, String drawerId,
                                              String arrowId, boolean compatibleTable) {
        StringBuilder sb = new StringBuilder();
        boolean isOpen = !compatibleTable; // Assuming incompatible tables are open by default
        String arrowDirection = compatibleTable ? " open" : "";

        sb.append(String.format("<h4 class=\"drawer-toggle\" onclick=\"toggleDrawer('%s', '%s')\">\n", drawerId,
                        arrowId))
                .append(String.format("<span id=\"%s\" class=\"arrow%s\"></span>\n", arrowId, arrowDirection))
                .append(String.format("<span>%s XML Element Tags</span>\n", tagType))
                .append("</h4>\n")
                .append(String.format("<div class=\"drawer%s\" id=\"%s\">\n", isOpen ? " open" : "", drawerId))
                .append(String.format("<table%s>\n", compatibleTable ? " class=\"green-table\"" : ""))
                .append("<tr><th>Element Tag</th><th>Weight</th><th>Occurrences</th><th>Total Weight</th></tr>\n");

        tagMap.forEach((elementTag, occurrence) -> {
            int weight = MuleXMLTag.getWeightFromTag(elementTag);
            int totalWeight = weight * occurrence;
            sb.append(String.format("<tr><td>%s</td><td>%d</td><td>%d</td><td>%d</td></tr>\n",
                    elementTag, weight, occurrence, totalWeight));
        });

        if (tagMap.isEmpty()) {
            sb.append(String.format("<tr><td>%s</td><td>%s</td><td>%s</td><td>%s</td></tr>\n", "-", "-", "-", "-"));
        }

        sb.append("</table>\n").append("</div>\n");
        return sb.toString();
    }

    private static String generateTagWeightReferenceTableSection() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h4 class=\"drawer-toggle\" onclick=\"toggleDrawer('tagWeightsDrawer', 'tagWeightsArrow')\">\n")
                .append("<span id=\"tagWeightsArrow\" class=\"arrow open\"></span>\n")
                .append("<span>Tag Weight Map</span>\n")
                .append("</h4>\n")
                .append("<div class=\"drawer\" id=\"tagWeightsDrawer\">\n")
                .append("<div class=\"scrollable-table\">\n")
                .append("<table class=\"blue-table\">\n")
                .append("<tr><th>Tag</th><th>Weight</th></tr>\n");

        MuleXMLTag.TAG_WEIGHTS_MAP.forEach((tag, weight) ->
                sb.append(String.format("<tr><td>%s</td><td>%d</td></tr>\n", tag, weight)));

        sb.append("</table>\n").append("</div>\n").append("</div>\n");
        return sb.toString();
    }

    private static String generateHtmlFooter() {
        return """
                <footer>
                <p>Report generated on: <span id="datetime"></span></p>
                </footer>
                <script>
                document.getElementById("datetime").innerHTML = new Date().toLocaleString();
                function toggleDrawer(drawerId, arrowId) {
                var drawer = document.getElementById(drawerId);
                var arrow = document.getElementById(arrowId);
                drawer.classList.toggle("open");
                arrow.classList.toggle("open");
                }
                </script>
                </body>
                </html>
                """;
    }

    private static int calculateTotalWeight(LinkedHashMap<String, Integer> tagMap) {
        return tagMap.entrySet().stream()
                .mapToInt(entry -> MuleXMLTag.getWeightFromTag(entry.getKey()) * entry.getValue())
                .sum();
    }

    private static String generateDataWeaveTagsSection(DWConversionStats stats) {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("<h4 class=\"drawer-toggle\" onclick=\"toggleDrawer('%s', '%s')\">\n", "dwTagsDrawer",
                        "dwArrow"))
                .append(String.format("<span id=\"%s\" class=\"arrow open\"></span>\n", "dwArrow"))
                .append("<span>DataWeave Constructs</span>\n")
                .append("</h4>\n")
                .append(String.format("<div class=\"drawer open\" id=\"%s\">\n", "dwTagsDrawer"))
                .append("<table class=\"green-table\">\n")
                .append("<tr><th>Construct</th><th>Weight</th><th>Encountered</th><th>Converted</th><th>Total " +
                        "Weight</th><th>Converted Weight</th><th>Success %%</th></tr>\n");

        for (DWConstruct construct : stats.getEncountered().keySet()) {
            int encountered = stats.getEncountered().getOrDefault(construct, 0);
            int converted = stats.getConverted().getOrDefault(construct, 0);
            int weight = construct.weight();
            int totalWeight = encountered * weight;
            int convertedWeight = converted * weight;
            double success = encountered == 0 ? 0 : (converted * 100.0 / encountered);
            sb.append(String.format("<tr><td>%s</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td><td>%d</td>" +
                            "<td>%.2f%%</td></tr>\n",
                    construct.component(), weight, encountered, converted, totalWeight, convertedWeight, success));
        }

        if (stats.getEncountered().isEmpty()) {
            sb.append("<tr><td colspan='7'>No DataWeave constructs found</td></tr>\n");
        }

        sb.append("</table>\n</div>\n");
        return sb.toString();
    }

    private static String generateDWConstructWeightReferenceTableSection() {
        StringBuilder sb = new StringBuilder();
        sb.append("<h4 class=\"drawer-toggle\" onclick=\"toggleDrawer('dwConstructWeightsDrawer', " +
                        "'dwConstructWeightsArrow')\">\n")
                .append("<span id=\"dwConstructWeightsArrow\" class=\"arrow open\"></span>\n")
                .append("<span>DataWeave Construct Weight Map</span>\n")
                .append("</h4>\n")
                .append("<div class=\"drawer\" id=\"dwConstructWeightsDrawer\">\n")
                .append("<div class=\"scrollable-table\">\n")
                .append("<table class=\"blue-table\">\n")
                .append("<tr><th>Construct</th><th>Weight</th></tr>\n");

        for (DWConstruct construct : DWConstruct.values()) {
            sb.append(String.format("<tr><td>%s</td><td>%d</td></tr>\n",
                    construct.component().toUpperCase(Locale.ROOT), construct.weight()));
        }

        sb.append("</table>\n</div>\n</div>\n");
        return sb.toString();
    }


}
