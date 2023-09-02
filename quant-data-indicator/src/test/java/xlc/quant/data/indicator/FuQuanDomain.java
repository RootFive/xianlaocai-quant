/**
 * 
 */
package xlc.quant.data.indicator;

import java.math.BigDecimal;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 复权行情：前复权和后复权模型都支持
 * @author Rootfive
 */
@Data
@NoArgsConstructor
public class FuQuanDomain {
	

    /** 开盘价(元) */
    private BigDecimal open;

    /** 最高价(元) */
    private BigDecimal high;

    /** 最低价(元) */
    private BigDecimal low;
    /** 收盘价(元) */
    private BigDecimal close;
    
    /** 前收价(元) */
    private BigDecimal preClose;

    /** 涨跌额,价格变动(元) */
    private BigDecimal priceChange;
	
}
