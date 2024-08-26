package async.model;

import java.math.BigDecimal;

public record Quote(String symbol, BigDecimal price, long timestamp){

}

