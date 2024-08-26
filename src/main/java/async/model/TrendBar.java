package async.model;

import java.math.BigDecimal;
import java.util.Objects;

public class TrendBar {
    private final String symbol;
    private final TrendBarPeriod period;
    private final BigDecimal openPrice;
    private BigDecimal closePrice;
    private BigDecimal highPrice;
    private BigDecimal lowPrice;
    private final long openTimestamp;
    private long closeTimestamp;

    public TrendBar(String symbol, TrendBarPeriod period, BigDecimal openPrice, BigDecimal closePrice,
                    BigDecimal highPrice, BigDecimal lowPrice, long openTimestamp, long closeTimestamp) {
        this.symbol = symbol;
        this.period = period;
        this.openPrice = openPrice;
        this.closePrice = closePrice;
        this.highPrice = highPrice;
        this.lowPrice = lowPrice;
        this.openTimestamp = openTimestamp;
        this.closeTimestamp = closeTimestamp;
    }


    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (object == null || getClass() != object.getClass()) return false;
        TrendBar trendBar = (TrendBar) object;
        return openTimestamp == trendBar.openTimestamp && Objects.equals(symbol, trendBar.symbol) && period == trendBar.period && Objects.equals(openPrice, trendBar.openPrice);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, period, openPrice, openTimestamp);
    }

    public String getSymbol() {
        return symbol;
    }

    public TrendBarPeriod getPeriod() {
        return period;
    }

    public BigDecimal getOpenPrice() {
        return openPrice;
    }

    public BigDecimal getClosePrice() {
        return closePrice;
    }

    public void setClosePrice(BigDecimal closePrice) {
        this.closePrice = closePrice;
    }

    public BigDecimal getHighPrice() {
        return highPrice;
    }

    public void setHighPrice(BigDecimal highPrice) {
        this.highPrice = highPrice;
    }

    public BigDecimal getLowPrice() {
        return lowPrice;
    }

    public void setLowPrice(BigDecimal lowPrice) {
        this.lowPrice = lowPrice;
    }

    public long getOpenTimestamp() {
        return openTimestamp;
    }

    public long getCloseTimestamp() {
        return closeTimestamp;
    }

    public void setCloseTimestamp(long closeTimestamp) {
        this.closeTimestamp = closeTimestamp;
    }
}

