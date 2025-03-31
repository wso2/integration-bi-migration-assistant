package dataweave.converter;

import java.util.LinkedHashMap;
import java.util.Map;

public class DWConversionStats {
    private final Map<DWConstruct, Integer> encountered = new LinkedHashMap<>();
    private final Map<DWConstruct, Integer> converted = new LinkedHashMap<>();

    public void record(DWConstruct construct, boolean isConverted) {
        encountered.merge(construct, 1, Integer::sum);
        if (isConverted) {
            converted.merge(construct, 1, Integer::sum);
        }
    }

    public int getTotalWeight() {
        return encountered.entrySet().stream()
                .mapToInt(e -> e.getKey().weight() * e.getValue()).sum();
    }

    public int getConvertedWeight() {
        return converted.entrySet().stream()
                .mapToInt(e -> e.getKey().weight() * e.getValue()).sum();
    }

    public double getConversionPercentage() {
        int total = getTotalWeight();
        return total == 0 ? 0 : (100.0 * getConvertedWeight() / total);
    }

    public Map<DWConstruct, Integer> getEncountered() {
        return encountered;
    }

    public Map<DWConstruct, Integer> getConverted() {
        return converted;
    }
}
