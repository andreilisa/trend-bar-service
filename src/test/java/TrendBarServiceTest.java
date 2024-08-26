import async.model.Quote;
import async.model.TrendBar;
import async.model.TrendBarPeriod;
import async.service.TrendBarService;
import async.storage.TrendBarStorage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

public class TrendBarServiceTest {
    private TrendBarStorage storage;
    private TrendBarService service;

    @BeforeEach
    public void setup() {
        storage = new TrendBarStorage();
        service = new TrendBarService(storage);
    }

    @AfterEach
    public void teardown() throws InterruptedException {
        service.shutdown();
        storage.clear();
    }

    @Test
    public void testAddQuote() throws InterruptedException {

        Quote quote1 = new Quote("APPLE", new BigDecimal("150.00"), System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(2));
        Quote quote2 = new Quote("APPLE", new BigDecimal("155.00"), System.currentTimeMillis() - TimeUnit.MINUTES.toMillis(1));
        Quote quote3 = new Quote("APPLE", new BigDecimal("145.00"), System.currentTimeMillis());


        service.addQuote(quote1);
        service.addQuote(quote2);
        service.addQuote(quote3);

       TimeUnit.SECONDS.sleep(2);
        List<TrendBar> trendBars = storage.getTrendBars("APPLE");
        assertEquals(2, trendBars.size());

        TrendBar tb1 = trendBars.get(0);
        TrendBar tb2 = trendBars.get(1);

        assertEquals("APPLE", tb1.getSymbol());
        assertEquals(TrendBarPeriod.M1, tb1.getPeriod());
        assertEquals(new BigDecimal("150.00"), tb1.getOpenPrice());
        assertEquals(new BigDecimal("150.00"), tb1.getHighPrice());
        assertEquals(new BigDecimal("150.00"), tb1.getLowPrice());
        assertEquals(new BigDecimal("150.00"), tb1.getClosePrice());

        assertEquals("APPLE", tb2.getSymbol());
        assertEquals(TrendBarPeriod.M1, tb2.getPeriod());
        assertEquals(new BigDecimal("155.00"), tb2.getOpenPrice());
        assertEquals(new BigDecimal("155.00"), tb2.getHighPrice());
        assertEquals(new BigDecimal("155.00"), tb2.getLowPrice());
        assertEquals(new BigDecimal("155.00"), tb2.getClosePrice());
    }

    @Test
    public void testGetTrendBars() throws Exception {
        // Arrange
        TrendBar tb1 = new TrendBar("APPLE", TrendBarPeriod.M1, new BigDecimal("150.0"),
                new BigDecimal("150.0"), new BigDecimal("150.0"),
                new BigDecimal("150.0"), 1724947200000L, 1724947260000L);

        TrendBar tb2 = new TrendBar("APPLE", TrendBarPeriod.M1, new BigDecimal("155.0"),
                new BigDecimal("155.0"), new BigDecimal("155.0"),
                new BigDecimal("155.0"), 1724947260000L, 1724947320000L);

        storage.saveTrendBar("APPLE", tb1);
        storage.saveTrendBar("APPLE", tb2);

        List<TrendBar> trendBars = service.getTrendBars("APPLE", TrendBarPeriod.M1, 1724947200000L, 1724947260000L).get();

        assertEquals(1, trendBars.size());
        TrendBar result = trendBars.getFirst();

        assertEquals("APPLE", result.getSymbol());
        assertEquals(TrendBarPeriod.M1, result.getPeriod());
        assertEquals(new BigDecimal("150.0"), result.getOpenPrice());
        assertEquals(new BigDecimal("150.0"), result.getHighPrice());
        assertEquals(new BigDecimal("150.0"), result.getLowPrice());
        assertEquals(new BigDecimal("150.0"), result.getClosePrice());
        assertEquals(1724947200000L, result.getOpenTimestamp());
        assertEquals(1724947260000L, result.getCloseTimestamp());
    }
}
