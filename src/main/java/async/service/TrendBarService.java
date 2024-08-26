package async.service;

import async.model.Quote;
import async.model.TrendBar;
import async.model.TrendBarPeriod;
import async.storage.TrendBarStorage;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

public class TrendBarService {

        private final TrendBarStorage storage;
        public final Map<String, TrendBar> currentBars = new ConcurrentHashMap<>();
        private final ExecutorService quoteProcessingPool = Executors.newSingleThreadExecutor();
        private final ExecutorService retrievalPool = Executors.newVirtualThreadPerTaskExecutor();

        public TrendBarService(TrendBarStorage storage) {
            this.storage = storage;
        }

        public void addQuote(Quote quote) {
            quoteProcessingPool.submit(() -> processQuote(quote));
        }

        private void processQuote(Quote quote) {
            String symbol = quote.symbol();
            BigDecimal price = quote.price();
            long timestamp = quote.timestamp();

            TrendBar currentBar = currentBars.get(symbol);
            if (currentBar == null || timestamp >= currentBar.getCloseTimestamp()) {
                if (currentBar != null) {
                    storage.saveTrendBar(symbol, currentBar);
                }
                TrendBarPeriod period = determinePeriod(timestamp);
                currentBar = new TrendBar(symbol, period, price, price, price, price, timestamp,
                        calculateCloseTimestamp(timestamp, period));
                currentBars.put(symbol, currentBar);
            } else {
                currentBar.setClosePrice(price);
                if (price.compareTo(currentBar.getHighPrice()) > 0) {
                    currentBar.setHighPrice(price);
                }
                if (price.compareTo(currentBar.getLowPrice()) < 0) {
                    currentBar.setLowPrice(price);
                }
            }
        }

    public CompletableFuture<List<TrendBar>> getTrendBars(String symbol, TrendBarPeriod period, long fromTimestamp, Long toTimestamp) {
            if (toTimestamp == null) {
                toTimestamp = System.currentTimeMillis();
            }
        Long finalToTimestamp = toTimestamp;
        return CompletableFuture.supplyAsync(() ->
                storage.getTrendBars(symbol).stream()
                        .filter(bar -> bar.getPeriod() == period)
                        .filter(bar -> bar.getCloseTimestamp() >= fromTimestamp)
                        .filter(bar -> bar.getCloseTimestamp() <= finalToTimestamp)
                        .collect(Collectors.toList()), retrievalPool);
    }

        public void shutdown() throws InterruptedException {
            quoteProcessingPool.shutdown();
            retrievalPool.shutdown();
            quoteProcessingPool.awaitTermination(30, TimeUnit.SECONDS);
            retrievalPool.awaitTermination(30, TimeUnit.SECONDS);
        }
        private long calculateCloseTimestamp(long openTimestamp, TrendBarPeriod period) {
            return switch (period) {
                case M1 -> openTimestamp + 60_000L;
                case H1 -> openTimestamp + 3_600_000L;
                case D1 -> openTimestamp + 86_400_000L;
            };
        }

         private TrendBarPeriod determinePeriod(long timestamp) {
            long minutes = (timestamp / 1000) / 60;
            if (minutes % 1440 == 0) {
                return TrendBarPeriod.D1;
            } else if (minutes % 60 == 0) {
                return TrendBarPeriod.H1;
            } else {
                return TrendBarPeriod.M1;
            }
        }
}
