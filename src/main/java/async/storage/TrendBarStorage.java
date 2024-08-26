package async.storage;

import async.model.TrendBar;
import async.model.TrendBarPeriod;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class TrendBarStorage {
    private final Map<String, List<TrendBar>> storage = new ConcurrentHashMap<>();


    public void saveTrendBar(String symbol, TrendBar trendBar) {
        storage.computeIfAbsent(symbol, k -> new java.util.ArrayList<>()).add(trendBar);
    }

    public List<TrendBar> getTrendBars(String symbol) {
        return storage.getOrDefault(symbol, List.of());
    }

    public void clear() {
        storage.clear();
    }
}
